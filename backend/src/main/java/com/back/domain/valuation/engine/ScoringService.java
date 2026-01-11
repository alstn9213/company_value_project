package com.back.domain.valuation.engine;

import com.back.domain.company.dto.response.CompanyScoreResponse;
import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.macro.entity.MacroEconomicData;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.macro.repository.MacroRepository;
import com.back.domain.valuation.calculator.MarketMetricCalculator;
import com.back.domain.valuation.constant.ScoreCategory;
import com.back.domain.valuation.model.MarketMetricsDto;
import com.back.domain.valuation.model.ScoreEvaluationResultDto;
import com.back.domain.valuation.model.ScoringDataDto;
import com.back.domain.valuation.policy.PenaltyPolicy;
import com.back.domain.valuation.strategy.ScoringAggregator;
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
  public void calculateScoresAndSave() {
    log.info("모든 기업의 평가 점수 계산을 시작합니다...");

    // 거시 경제 데이터는 모든 기업 평가에 쓰이니까 맨 먼저 정의하는게 효율적임
    MacroEconomicData macro = macroRepository.findTopByOrderByRecordedDateDesc()
            .orElseThrow(() -> new BusinessException(ErrorCode.MACRO_DATA_NOT_FOUND));

    List<Company> companies = companyRepository.findAll();

    for (Company company : companies) {
      try {
        processCompanyScore(company, macro);
      } catch (Exception e) {
        log.error("점수 계산 중 오류 발생 {}: {}", company.getName(), e.getMessage());
      }
    }
    log.info("모든 기업 점수 계산 완료.");
  }

  // 오케스트레이션 (데이터 준비 -> 계산 -> 저장) 헬퍼
  private void processCompanyScore(Company company, MacroEconomicData macro) {
    FinancialStatement fs = financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(company)
            .orElseThrow(() -> new BusinessException(ErrorCode.FINANCIAL_STATEMENT_NOT_FOUND));

    validateFinancialData(fs);

    StockPriceHistory latestStockData = stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(company)
            .orElseThrow(() -> new BusinessException(ErrorCode.LATEST_STOCK_NOT_FOUND));

    ScoreEvaluationResultDto result = calculateScore(fs, latestStockData.getClosePrice(), macro);

    saveScore(company, result);
  }

  // 점수 계산 헬퍼
  private ScoreEvaluationResultDto calculateScore(FinancialStatement fs, BigDecimal stockPrice, MacroEconomicData macro) {
    // 기업 가치 지표 계산
    MarketMetricsDto metrics = marketMetricCalculator.calculate(fs, stockPrice);

    // 점수 계산용 데이터 객체 생성
    ScoringDataDto scoringData = new ScoringDataDto(fs, metrics, stockPrice);

    // 전략 실행
    Map<ScoreCategory, Integer> componentScores = scoringAggregator.calculateAll(scoringData);

    // 페널티 계산
    int penalty = penaltyPolicy.calculatePenalty(fs, macro);

    // 최종 평가
    return scoreEvaluator.evaluate(componentScores, penalty, fs);
  }

  // 점수 상위 10등 회사들 가져오는 서비스
  public List<CompanyScoreResponse> getTopRankedCompanies() {
    return companyScoreRepository.findTop10ByOrderByTotalScoreDesc()
            .stream()
            .map(CompanyScoreResponse::from)
            .toList();
  }

  // 특정 회사의 점수 가져오는 서비스
  @Cacheable(value = "company_score", key = "#ticker", unless = "#result == null")
  public CompanyScoreResponse getScoreByTicker(String ticker) {
    Company company = companyRepository.findByTicker(ticker)
            .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

    return companyScoreRepository.findByCompany(company)
            .map(CompanyScoreResponse::from)
            .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_SCORE_NOT_FOUND));
  }



  // --- 기본 헬퍼 메서드 ---

  // NPE 방지를 위한 데이터 검증 헬퍼
  private void validateFinancialData(FinancialStatement fs) {
    BigDecimal equity = fs.getTotalEquity();
    BigDecimal netIncome = fs.getNetIncome();
    BigDecimal totalAssets = fs.getTotalAssets();

    if (equity == null || netIncome  == null || totalAssets == null) {
      log.warn("[데이터 누락] {}: 자본={} 순수익={} 자산={}", fs.getCompany().getName(), equity, netIncome, totalAssets);
      throw new BusinessException(ErrorCode.FINANCIAL_STATEMENT_NOT_FOUND);
    }
  }

  // 점수 저장 헬퍼
  private void saveScore(Company company, ScoreEvaluationResultDto result) {
    // 기존 점수를 조회하고 없으면 점수 객체 생성
    CompanyScore score = companyScoreRepository.findByCompany(company)
            .orElseGet(() -> CompanyScore.builder().company(company).build());

    score.updateScore(result);

    companyScoreRepository.save(score);
  }
}