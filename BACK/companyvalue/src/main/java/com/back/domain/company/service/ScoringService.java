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
import com.back.domain.company.service.analysis.dto.MarketMetricsDto;
import com.back.domain.company.service.analysis.dto.ScoreEvaluationResultDto;
import com.back.domain.company.service.analysis.dto.ScoringDataDto;
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
      String companyName = company.getName();

      try {
        FinancialStatement fs = financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(company)
                .orElseThrow(() -> new BusinessException(ErrorCode.FINANCIAL_STATEMENT_NOT_FOUND));
        calculateAndSaveScore(fs);

      } catch (BusinessException e) {
        log.warn("점수 계산 스킵 {}: {}", companyName, e.getMessage());

      } catch (Exception e) {
        log.error("점수 계산 중 오류 발생 {}: {}", companyName, e.getMessage());
      }
    }
    log.info("모든 기업 점수 계산 완료.");
  }

  // 특정 기업의 점수 계산하고 저장하는 헬퍼
  private void calculateAndSaveScore(FinancialStatement fs) {

    validateFinancialData(fs);

    MacroEconomicData macro = macroRepository.findTopByOrderByRecordedDateDesc()
            .orElseThrow(() -> new BusinessException(ErrorCode.MACRO_DATA_NOT_FOUND));

    StockPriceHistory latestStockData = stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(fs.getCompany())
            .orElseThrow(() -> new BusinessException(ErrorCode.LATEST_STOCK_NOT_FOUND));

    BigDecimal stockPrice = latestStockData.getClosePrice();

    // 기업의 가치들 계산
    MarketMetricsDto metrics = marketMetricCalculator.calculate(fs, stockPrice);

    // 점수 매기는데 필요한 데이터들을 모은 객체
    ScoringDataDto scoringData = new ScoringDataDto(fs, metrics, stockPrice);

    // 모든 전략 실행 (Map으로 결과 받음)
    Map<ScoreCategory, Integer> componentScores = scoringAggregator.calculateAll(scoringData);

    // 페널티 계산
    int penalty = penaltyPolicy.calculatePenalty(fs, macro);

    // 총점 계산
    ScoreEvaluationResultDto result = scoreEvaluator.evaluate(componentScores, penalty, fs);

    saveScore(fs.getCompany(), result);

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



  // --- 헬퍼 메서드 ---

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