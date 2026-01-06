package com.back.domain.company.service.analysis;

import com.back.domain.company.dto.response.CompanyScoreResponse;
import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.MarketMetrics;
import com.back.domain.company.service.analysis.dto.ScoringData;
import com.back.domain.company.service.analysis.policy.PenaltyPolicy;
import com.back.domain.company.service.analysis.strategy.ScoringAggregator;
import com.back.domain.company.service.analysis.strategy.components.InvestmentStrategy;
import com.back.domain.company.service.analysis.strategy.components.ProfitabilityStrategy;
import com.back.domain.company.service.analysis.strategy.components.StabilityStrategy;
import com.back.domain.company.service.analysis.strategy.components.ValuationStrategy;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.macro.repository.MacroRepository;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static com.back.domain.company.service.analysis.constant.ScoringConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScoringService {

  private final CompanyScoreRepository companyScoreRepository;
  private final MacroRepository macroRepository;
  private final CompanyRepository companyRepository;
  private final StockPriceHistoryRepository stockPriceHistoryRepository;
  private final FinancialStatementRepository financialStatementRepository;

  private final ScoringAggregator scoringAggregator;

  private final PenaltyPolicy penaltyPolicy;

  @Transactional
  public void calculateAllScores() {
    log.info("모든 기업의 평가 점수 계산을 시작합니다...");
    List<Company> companies = companyRepository.findAll();

    for (Company company : companies) {
      try {
        financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(company)
                .ifPresentOrElse(
                        this::calculateAndSaveScore,
                        () -> log.warn("재무 데이터 없음: {}", company.getTicker())
                );
      } catch (BusinessException e) {
        log.warn("점수 계산 스킵 (Ticker: {}): {}", company.getTicker(), e.getMessage());
      } catch (Exception e) {
        log.error("점수 계산 중 오류 발생 (Ticker: {}): {}", company.getTicker(), e.getMessage());
      }
    }
    log.info("모든 기업 점수 계산 완료.");
  }

  public List<CompanyScoreResponse> getTopRankedCompanies() {
    return companyScoreRepository.findTop10ByOrderByTotalScoreDesc()
            .stream()
            .map(CompanyScoreResponse::from)
            .toList();
  }

  @Cacheable(value = "company_score", key = "#ticker", unless = "#result == null")
  public CompanyScoreResponse getScoreByTicker(String ticker) {
    Company company = companyRepository.findByTicker(ticker)
            .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

    return companyScoreRepository.findByCompany(company)
            .map(CompanyScoreResponse::from)
            .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));
  }

  @Transactional
  public void calculateAndSaveScore(FinancialStatement fs) {
    validateFinancialData(fs);

    MacroEconomicData macro = macroRepository.findTopByOrderByRecordedDateDesc()
            .orElseThrow(() -> new BusinessException(ErrorCode.MACRO_DATA_NOT_FOUND));

    StockPriceHistory latestStock = stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(fs.getCompany());
    if (latestStock == null) {
      throw new BusinessException(ErrorCode.LATEST_STOCK_NOT_FOUND);
    }

    BigDecimal price = latestStock.getClosePrice();
    MarketMetrics metrics = calculateMarketMetrics(fs, price);
    ScoringData scoringData = new ScoringData(fs, metrics, price);

    // 모든 전략 실행 (Map으로 결과 받음)
    Map<ScoreCategory, Integer> scores = scoringAggregator.calculateAll(scoringData);

    // 각 영역의 점수 추출
    int stability = scores.getOrDefault(ScoreCategory.STABILITY, 0);
    int profitability = scores.getOrDefault(ScoreCategory.PROFITABILITY, 0);
    int valuation = scores.getOrDefault(ScoreCategory.VALUATION, 0);
    int investment = scores.getOrDefault(ScoreCategory.INVESTMENT, 0);

    // 기본 점수 계산
    int baseScore = stability + profitability + valuation + investment;

    // 페널티 계산
    int penalty = penaltyPolicy.calculatePenalty(fs, macro);

    // 총점 계산
    int totalScore = Math.max(0, Math.min(100, baseScore - penalty));

    // 등급 매기기
    String grade = calculateGrade(totalScore);

    // 기업 자체의 가치가 훌륭해서(PBR, PER이 낮음) 저점 매수하기 좋을 경우
    // 단, 자본 잠식은 걸러냄
    boolean isOpportunity = (valuation >= OPPORTUNITY_VALUATION_THRESHOLD)
            && (fs.getTotalEquity().compareTo(BigDecimal.ZERO) > 0);

    saveScore(
            fs,
            totalScore,
            stability,
            profitability,
            valuation,
            investment,
            grade,
            isOpportunity
    );
  }

  // --- 헬퍼 메서드 ---

  // NPE 방지를 위한 데이터 검증 헬퍼
  private void validateFinancialData(FinancialStatement fs) {
    if (fs.getTotalEquity() == null || fs.getNetIncome() == null || fs.getTotalAssets() == null) {
      throw new BusinessException(ErrorCode.INVALID_FINANCIAL_DATA);
    }
  }

  // 밸류 지표 계산 헬퍼
  private MarketMetrics calculateMarketMetrics(FinancialStatement fs, BigDecimal price) {
    Long totalSharesObj = fs.getCompany().getTotalShares();

    // 주식 수가 null이거나 0이면 PER/PBR 계산 불가 (방어 로직)
    if (totalSharesObj == null || totalSharesObj == 0) {
      return MarketMetrics.empty();
    }

    BigDecimal shares = BigDecimal.valueOf(totalSharesObj);

    // EPS (주당 순이익) = 순이익 / 주식수
    BigDecimal eps = fs.getNetIncome().divide(shares, 2, RoundingMode.HALF_UP);

    // PER (주가수익비율) = 주가 / EPS
    BigDecimal per = BigDecimal.ZERO;
    if (eps.compareTo(BigDecimal.ZERO) > 0) {
      per = price.divide(eps, 2, RoundingMode.HALF_UP);
    }

    // BPS (주당 순자산) = 자본총계 / 주식수
    BigDecimal bps = fs.getTotalEquity().divide(shares, 2, RoundingMode.HALF_UP);

    // PBR (주가순자산비율) = 주가 / BPS
    BigDecimal pbr = BigDecimal.ZERO;
    if (bps.compareTo(BigDecimal.ZERO) > 0) {
      pbr = price.divide(bps, 2, RoundingMode.HALF_UP);
    }

    return new MarketMetrics(eps, per, bps, pbr);
  }

  // 회사 등급 매기는 헬퍼
  private String calculateGrade(int score) {
    if(score >= GRADE_S_THRESHOLD) return "S";
    if(score >= GRADE_A_THRESHOLD) return "A";
    if(score >= GRADE_B_THRESHOLD) return "B";
    if(score >= GRADE_C_THRESHOLD) return "C";
    return "D";
  }

  // 점수 저장 헬퍼
  private void saveScore(FinancialStatement fs, int total, int stab, int prof, int val, int inv, String grade, boolean isOpportunity) {
    CompanyScore score = companyScoreRepository.findByCompany(fs.getCompany())
            .orElseGet(() -> CompanyScore.builder()
                    .company(fs.getCompany())
                    .build());

    score.updateScore(total, stab, prof, val, inv, grade, isOpportunity);
    companyScoreRepository.save(score);
  }
}