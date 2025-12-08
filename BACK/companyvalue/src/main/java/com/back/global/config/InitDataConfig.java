package com.back.global.config;

import com.back.domain.company.entity.Company;

import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.CompanyRepository;

import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitDataConfig {

    private final CompanyRepository companyRepository;
    private final FinancialStatementRepository financialStatementRepository;
    private final StockPriceHistoryRepository stockPriceHistoryRepository;

    private static final int YEARS_OF_DATA = 3;
    private static final LocalDate END_DATE = LocalDate.now();
    private static final LocalDate START_DATE = END_DATE.minusYears(YEARS_OF_DATA);


    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("[InitData] 가상의 미국 기업 데이터 확인 및 생성을 시작합니다...");
            List<Company> companies = createCompanyList();

            for (Company companyData : companies) {
                // 1. 기업 조회 혹은 생성
                Company company = companyRepository.findByTicker(companyData.getTicker())
                        .orElseGet(() -> {
                            log.info("[InitData] 신규 기업 생성: {}", companyData.getName());
                            return companyRepository.save(companyData);
                        });

                // 2. 재무 데이터 존재 여부 확인 (하나라도 있으면 스킵)
                boolean hasFinancials = financialStatementRepository.existsByCompany(company);

                if (!hasFinancials) {
                    log.info("[InitData] {}의 재무/주가 데이터를 생성합니다...", company.getName());

                    SectorProfile profile = SectorProfile.getProfile(company.getSector());

                    // 재무제표 생성
                    List<FinancialStatement> financials = generateFinancials(company, profile);
                    financialStatementRepository.saveAll(financials);

                    // 주가 데이터 생성
                    List<StockPriceHistory> stockHistory = generateStockPrices(company, financials, profile);
                    stockPriceHistoryRepository.saveAll(stockHistory);
                }
            }
            log.info("[InitData] 데이터 초기화 로직 완료.");
        };
    }

    private List<Company> createCompanyList() {
        return List.of(
                Company.builder().ticker("AAPL").name("Apple Inc.").sector("Technology").exchange("NASDAQ").build(),
                createCompany("NXAI", "NextGen AI Solutions", "Technology", "NASDAQ"),
                createCompany("QBIT", "Quantum Bit Computing", "Technology", "NASDAQ"),
                createCompany("CYBR", "Cyber Shield Systems", "Technology", "NASDAQ"),
                createCompany("CLOUD", "Sky High Cloud Services", "Technology", "NASDAQ"),
                createCompany("DATA", "Big Data Analytics", "Technology", "NASDAQ"),
                createCompany("VIRT", "Virtual Reality Corp", "Technology", "NASDAQ"),
                createCompany("LBNK", "Liberty National Bank", "Financial Services", "NYSE"),
                createCompany("GFIN", "Global Finance Group", "Financial Services", "NYSE"),
                createCompany("WALTH", "Wealth Management Partners", "Financial Services", "NYSE"),
                createCompany("SURE", "Surety Insurance Co", "Financial Services", "NYSE"),
                createCompany("MEDI", "MediCare Plus Solutions", "Healthcare", "NYSE"),
                createCompany("BIOX", "BioGenix Labs", "Healthcare", "NASDAQ"),
                createCompany("NANO", "Nano Cure Technologies", "Healthcare", "NASDAQ"),
                createCompany("EVMT", "Future EV Motors", "Consumer Cyclical", "NASDAQ"),
                createCompany("LUXE", "Luxury Brands Holdings", "Consumer Cyclical", "NYSE"),
                createCompany("FOOD", "Organic Whole Foods", "Consumer Defensive", "NYSE"),
                createCompany("DRNK", "Global Beverage Inc.", "Consumer Defensive", "NYSE"),
                createCompany("AERO", "AeroSpace Dynamics", "Industrial", "NYSE"),
                createCompany("ROBO", "Robotics Automation", "Industrial", "NASDAQ"),
                createCompany("CHEM", "Advanced Chemical Works", "Basic Materials", "NYSE")
        );
    }

    // --- 헬퍼 메서드 ---

    private Company createCompany(String ticker, String name, String sector, String exchange) {
        return Company.builder()
                .ticker(ticker)
                .name(name)
                .sector(sector)
                .exchange(exchange)
                .build();
    }

    private List<FinancialStatement> generateFinancials(Company company, SectorProfile profile) {
        List<FinancialStatement> list = new ArrayList<>();
        Random random = new Random();

        // 초기 연 매출 설정 (섹터별 기본값 + 랜덤 변동)
        double currentAnnualRevenue = profile.baseRevenue * (0.8 + random.nextDouble() * 0.4);

        for (int i = 0; i < YEARS_OF_DATA * 4; i++) { // 3년 * 4분기 = 12번 반복
            int year = START_DATE.getYear() + (i / 4);
            int quarter = (i % 4) + 1;

            // 성장 적용 (분기별 성장 + 약간의 노이즈)
            double growthFactor = 1 + (profile.growthRate / 4.0) + (random.nextGaussian() * 0.01);
            currentAnnualRevenue *= growthFactor;

            // 분기 매출 (계절성 반영: 4분기에 매출 증가 등)
            double seasonalFactor = (quarter == 4) ? 1.1 : 0.97;
            BigDecimal revenue = BigDecimal.valueOf(currentAnnualRevenue / 4.0 * seasonalFactor)
                    .setScale(2, RoundingMode.HALF_UP);

            // 이익 계산
            BigDecimal operatingProfit = revenue.multiply(BigDecimal.valueOf(profile.operatingMargin + random.nextGaussian() * 0.02));
            BigDecimal netIncome = operatingProfit.multiply(BigDecimal.valueOf(0.75)); // 법인세 등 차감 가정

            // 재무상태표 계산 (자산 = 부채 + 자본)
            // 자산 회전율을 통해 자산 규모 추정 (Asset Turnover)
            BigDecimal totalAssets = revenue.multiply(BigDecimal.valueOf(4.0)).multiply(BigDecimal.valueOf(profile.assetTurnoverMultiplier));
            BigDecimal totalLiabilities = totalAssets.multiply(BigDecimal.valueOf(profile.debtRatio));
            BigDecimal totalEquity = totalAssets.subtract(totalLiabilities);

            // 현금흐름 및 투자
            BigDecimal operatingCashFlow = operatingProfit.multiply(BigDecimal.valueOf(1.1)); // 보통 이익보다 현금흐름이 큼
            BigDecimal rnd = revenue.multiply(BigDecimal.valueOf(profile.rndRatio));
            BigDecimal capex = revenue.multiply(BigDecimal.valueOf(profile.capexRatio));

            list.add(FinancialStatement.builder()
                    .company(company)
                    .year(year)
                    .quarter(quarter)
                    .revenue(revenue)
                    .operatingProfit(operatingProfit)
                    .netIncome(netIncome)
                    .totalAssets(totalAssets)
                    .totalLiabilities(totalLiabilities)
                    .totalEquity(totalEquity)
                    .operatingCashFlow(operatingCashFlow)
                    .researchAndDevelopment(rnd)
                    .capitalExpenditure(capex)
                    .build());
        }
        return list;
    }

    private List<StockPriceHistory> generateStockPrices(Company company, List<FinancialStatement> financials, SectorProfile profile) {
        List<StockPriceHistory> history = new ArrayList<>();
        Random random = new Random();

        // 초기 주가 설정
        // 첫 분기 순이익 연환산 -> PER 적용 -> 적정 시총 -> 임의의 주식수(100만주 가정)로 나눠 주가 산출
        BigDecimal initialNetIncome = financials.get(0).getNetIncome();
        double annualEPS = initialNetIncome.doubleValue() * 4 / 1_000_000.0; // 주당 순이익
        double currentPrice = Math.max(10.0, annualEPS * profile.peRatio); // 최소 $10 보장

        // 날짜별 루프
        LocalDate currentDate = START_DATE;
        int financialIndex = 0;

        while (!currentDate.isAfter(END_DATE)) {
            // 주말 제외
            if (currentDate.getDayOfWeek().getValue() <= 5) {

                // 현재 시점에 적용되는 가장 최근 분기 실적 찾기
                FinancialStatement currentFS = financials.get(financialIndex);
                if (financialIndex < financials.size() - 1) {
                    // 다음 분기 실적 날짜(가정)를 지났다면 인덱스 증가
                    LocalDate nextQuarterDate = getQuarterEndDate(financials.get(financialIndex + 1));
                    if (currentDate.isAfter(nextQuarterDate)) {
                        financialIndex++;
                        currentFS = financials.get(financialIndex);
                    }
                }

                // 적정 주가(Fair Value) 계산: (최근 분기 순이익 * 4) / 100만주 * PER
                double targetPrice = (currentFS.getNetIncome().doubleValue() * 4 / 1_000_000.0) * profile.peRatio;

                // 주가 변동 로직:
                // 1. Drift: 적정 주가로 수렴하려는 힘 (Mean Reversion)
                double drift = (targetPrice - currentPrice) * 0.005; // 천천히 따라감

                // 2. Volatility: 랜덤 등락 (섹터별 변동성 + 시장 노이즈)
                double shock = currentPrice * random.nextGaussian() * (profile.volatility / Math.sqrt(252));

                currentPrice += drift + shock;
                if(currentPrice < 1.0) currentPrice = 1.0; // 동전주 방지

                history.add(StockPriceHistory.builder()
                        .company(company)
                        .recordedDate(currentDate)
                        .closePrice(BigDecimal.valueOf(currentPrice).setScale(2, RoundingMode.HALF_UP))
                        .build());
            }
            currentDate = currentDate.plusDays(1);
        }
        return history;
    }

    // 분기 종료일
    private LocalDate getQuarterEndDate(FinancialStatement fs) {
        int month = fs.getQuarter() * 3;
        // 해당 년도 해당 분기의 마지막 날 대략 계산 (3/31, 6/30...)
        return LocalDate.of(fs.getYear(), month, 1).plusMonths(1).minusDays(1);
    }

    // --- 섹터별 특성 정의 클래스 (내부 클래스) ---
    // 더미 데이터 생성을 위해 딱 한번만 쓰는 로직이라 내부 메서드로 한곳에 가두어 둠.
    // 나중에 이 부분이 서비스 로직(예: 포트폴리오 분석 기능 등)에서도 사용된다면, Enum 이나 Entity로 분리.
    @RequiredArgsConstructor
    static class SectorProfile {
        final double baseRevenue;      // 기본 매출 규모 (단위 무시, 상대적 크기)
        final double growthRate;       // 연간 성장률 (0.1 = 10%)
        final double operatingMargin;  // 영업이익률 (0.2 = 20%)
        final double debtRatio;        // 부채비율 (Total Liab / Total Assets)
        final double assetTurnoverMultiplier; // 자산회전율 역수 (높을수록 자산이 많이 필요)
        final double rndRatio;         // 매출 대비 R&D 비율
        final double capexRatio;       // 매출 대비 설비투자 비율
        final double peRatio;          // 적정 PER (주가수익비율)
        final double volatility;       // 연간 주가 변동성

        static SectorProfile getProfile(String sector) {
            switch(sector) {
                case "Technology": // 고성장, 고마진, 고변동성, R&D 높음
                    return new SectorProfile(500000000, 0.15, 0.25, 0.4, 1.5, 0.15, 0.05, 30.0, 0.40);
                case "Financial Services": // 저성장, 고부채(레버리지), 안정적
                    return new SectorProfile(800000000, 0.05, 0.30, 0.85, 5.0, 0.01, 0.02, 12.0, 0.20);
                case "Healthcare": // R&D 매우 높음, 적당한 성장
                    return new SectorProfile(400000000, 0.10, 0.15, 0.5, 1.2, 0.20, 0.05, 25.0, 0.30);
                case "Consumer Cyclical": // 경기민감, 중간 성장
                    return new SectorProfile(300000000, 0.08, 0.10, 0.6, 1.0, 0.03, 0.05, 18.0, 0.35);
                case "Consumer Defensive": // 저성장, 저변동성, 필수재
                    return new SectorProfile(600000000, 0.03, 0.08, 0.5, 1.0, 0.01, 0.03, 15.0, 0.15);
                case "Industrial": // 고정비(Capex) 높음, 경기민감
                    return new SectorProfile(450000000, 0.06, 0.12, 0.6, 1.8, 0.02, 0.10, 16.0, 0.25);
                case "Basic Materials": // 원자재, Capex 매우 높음
                    return new SectorProfile(400000000, 0.04, 0.15, 0.5, 2.0, 0.01, 0.15, 14.0, 0.30);
                default:
                    return new SectorProfile(300000000, 0.05, 0.10, 0.5, 1.0, 0.05, 0.05, 20.0, 0.25);
            }
        }
    }
}
