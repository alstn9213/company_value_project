package com.back.domain.company.service.analysis;

import com.back.domain.company.dto.response.CompanyScoreResponse;
import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.domain.company.service.analysis.dto.ScoringData;
import com.back.domain.company.service.analysis.policy.PenaltyPolicy;
import com.back.domain.company.service.analysis.strategy.InvestmentStrategy;
import com.back.domain.company.service.analysis.strategy.ProfitabilityStrategy;
import com.back.domain.company.service.analysis.strategy.StabilityStrategy;
import com.back.domain.company.service.analysis.strategy.ValuationStrategy;
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

    private final ObjectMapper objectMapper;

    // Strategies (점수 계산 전략들)
    private final StabilityStrategy stabilityStrategy;
    private final ProfitabilityStrategy profitabilityStrategy;
    private final ValuationStrategy valuationStrategy;
    private final InvestmentStrategy investmentStrategy;

    // Policies (페널티 정책)
    private final PenaltyPolicy penaltyPolicy;

    @Transactional
    public void calculateAllScores() {
        log.info("모든 기업의 가치 평가 점수 계산을 시작합니다...");
        List<Company> companies = companyRepository.findAll();

        for (Company company : companies) {
            try {
                // 각 기업의 가장 최신 재무제표 조회 (연도, 분기 내림차순)
                financialStatementRepository.findTopByCompanyOrderByYearDescQuarterDesc(company)
                        .ifPresentOrElse(
                                this::calculateAndSaveScore,
                                () -> log.warn("재무 데이터 없음: {}", company.getTicker())
                        );
            } catch (Exception e) {
                log.error("점수 계산 중 오류 발생 (Ticker: {}): {}", company.getTicker(), e.getMessage());
                // 개별 기업 실패가 전체 프로세스를 멈추지 않도록 로그만 찍고 넘어감
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
        MacroEconomicData macro = macroRepository.findTopByOrderByRecordedDateDesc()
                .orElseThrow(() -> new BusinessException(ErrorCode.MACRO_DATA_NOT_FOUND));

        StockPriceHistory latestStock = stockPriceHistoryRepository.findTopByCompanyOrderByRecordedDateDesc(fs.getCompany());
        if(latestStock == null) {
            log.error("{}의 최근 주가 데이터가 없습니다.", fs.getCompany().getName());
            return;
        }

        BigDecimal price = latestStock.getClosePrice();
        JsonNode calculatedOverview = createCalculatedOverview(fs, price);
        ScoringData scoringData = new ScoringData(fs, calculatedOverview, price);

        // 기본 점수 계산 (Strategy Pattern 활용)
        int stability = stabilityStrategy.calculate(scoringData);
        int profitability = profitabilityStrategy.calculate(scoringData);
        int valuation = valuationStrategy.calculate(scoringData);
        int investment = investmentStrategy.calculate(scoringData);
        int baseScore = stability + profitability + valuation + investment;
        // 페널티
        int penalty = penaltyPolicy.calculatePenalty(fs, macro);
        // 총점 계산
        int totalScore = Math.max(0, Math.min(100, baseScore - penalty));
        // 등급 매기기
        String grade = calculateGrade(totalScore);

        // 페널티는 존재하지만, 기업 자체의 가치는 훌륭해서(PBR, PER이 높음) 저점 매수하기 좋을 경우
        // 단, 자본 잠식은 걸러냄
        boolean isOpportunity = (penalty > 0)
                && (valuation >= OPPORTUNITY_VALUATION_THRESHOLD)
                && (fs.getTotalEquity().compareTo(BigDecimal.ZERO) > 0);

        saveScore(fs, totalScore, stability, profitability, valuation, investment, grade, isOpportunity);
    }

    // --- 헬퍼 메서드: 내부 데이터로 밸류 지표 계산 ---
    private JsonNode createCalculatedOverview(FinancialStatement fs, BigDecimal price) {
        ObjectNode overview = objectMapper.createObjectNode();
        long totalShares = fs.getCompany().getTotalShares();

        // 주식 수가 0이면 계산 불가 (방어 로직)
        if(totalShares == 0) return overview;

        BigDecimal shares = BigDecimal.valueOf(totalShares);

        // EPS (주당 순이익) = 순이익 / 주식수
        BigDecimal eps = fs.getNetIncome().divide(shares, 2, RoundingMode.HALF_UP);
        overview.put("EPS", eps.toString());

        // PER (주가수익비율) = 주가 / EPS
        if (eps.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal per = price.divide(eps, 2, RoundingMode.HALF_UP);
            overview.put("PERRatio", per.toString()); // 키 이름은 Strategy에서 사용하는 것과 맞춰야 함
        } else {
            overview.put("PERRatio", "0"); // 적자 기업
        }

        // BPS (주당 순자산) = 자본총계 / 주식수
        BigDecimal bps = fs.getTotalEquity().divide(shares, 2, RoundingMode.HALF_UP);
        overview.put("BookValue", bps.toString());

        // PBR (주가순자산비율) = 주가 / BPS
        if (bps.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal pbr = price.divide(bps, 2, RoundingMode.HALF_UP);
            overview.put("PriceToBookRatio", pbr.toString());
        } else {
            overview.put("PriceToBookRatio", "0");
        }

        return overview;
    }



    // --- 헬퍼 메서드 ---

    // 회사 등급 매기는 헬퍼 메서드
    private String calculateGrade(int score) {
        if(score >= GRADE_S_THRESHOLD) return "S";
        if(score >= GRADE_A_THRESHOLD) return "A";
        if(score >= GRADE_B_THRESHOLD) return "B";
        if(score >= GRADE_C_THRESHOLD) return "C";
        return "D";
    }

    // 점수 저장하는 헬퍼 메서드
    private void saveScore(FinancialStatement fs, int total, int stab, int prof, int val, int inv, String grade, boolean isOpportunity) {
        CompanyScore score = companyScoreRepository.findByCompany(fs.getCompany())
                .orElseGet(() -> CompanyScore.builder()
                        .company(fs.getCompany())
                        .build());

        score.updateScore(total, stab, prof, val, inv, grade, isOpportunity);
        companyScoreRepository.save(score);
    }
}