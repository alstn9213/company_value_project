package com.back.domain.company.service;

import com.back.domain.company.dto.response.CompanyScoreResponse;
import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.service.analysis.MarketMetricCalculator;
import com.back.domain.company.service.analysis.ScoreEvaluator;
import com.back.domain.company.service.analysis.constant.ScoreCategory;
import com.back.domain.company.service.analysis.dto.MarketMetrics;
import com.back.domain.company.service.analysis.dto.ScoreEvaluationResult;
import com.back.domain.company.service.analysis.dto.ScoringData;
import com.back.domain.company.service.analysis.policy.PenaltyPolicy;
import com.back.domain.company.service.analysis.strategy.ScoringAggregator;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.macro.repository.MacroRepository;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

  private final MarketMetricCalculator marketMetricCalculator;
  private final ScoreEvaluator scoreEvaluator;

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

  // 점수 계산하고 저장하는 헬퍼
  private void calculateAndSaveScore(FinancialStatement fs) {
    validateFinancialData(fs);

    MacroEconomicData macro = macroRepository.findTopByOrderByRecordedDateDesc()
            .orElseThrow(() -> new BusinessException(ErrorCode.MACRO_DATA_NOT_FOUND));

    StockPriceHistory latestStock = stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(fs.getCompany());
    if (latestStock == null) {
      throw new BusinessException(ErrorCode.LATEST_STOCK_NOT_FOUND);
    }

    BigDecimal price = latestStock.getClosePrice();
    MarketMetrics metrics = marketMetricCalculator.calculate(fs, price);
    ScoringData scoringData = new ScoringData(fs, metrics, price);

    // 모든 전략 실행 (Map으로 결과 받음)
    Map<ScoreCategory, Integer> componentScores = scoringAggregator.calculateAll(scoringData);

    // 페널티 계산
    int penalty = penaltyPolicy.calculatePenalty(fs, macro);

    // 총점 계산
    ScoreEvaluationResult result = scoreEvaluator.evaluate(componentScores, penalty, fs);

    saveScore(fs.getCompany(), result);

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
            .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_SCORE_NOT_FOUND));
  }



  // --- 헬퍼 메서드 ---

  // NPE 방지를 위한 데이터 검증 헬퍼
  private void validateFinancialData(FinancialStatement fs) {
    if (fs.getTotalEquity() == null || fs.getNetIncome() == null || fs.getTotalAssets() == null) {
      throw new BusinessException(ErrorCode.INVALID_FINANCIAL_DATA);
    }
  }

  // 점수 저장 헬퍼
  private void saveScore(Company company, ScoreEvaluationResult result) {
    CompanyScore score = companyScoreRepository.findByCompany(company)
            .orElseGet(() -> CompanyScore.builder().company(company).build());

    score.updateScore(
            result.totalScore(),
            result.stability(),
            result.profitability(),
            result.valuation(),
            result.investment(),
            result.grade(),
            result.isOpportunity()
    );
    companyScoreRepository.save(score);
  }
}