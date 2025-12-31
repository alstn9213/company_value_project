package com.back.global.config.init;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.entity.StockPriceHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class DummyDataGenerator {

    private static final int YEARS_OF_DATA = 3;
    private static final LocalDate END_DATE = LocalDate.now();
    private static final LocalDate START_DATE = END_DATE.minusYears(YEARS_OF_DATA);
    private static final long TOTAL_SHARES = 100_000_000L; // 1억 주 가정
    private final Random random = new Random();

    // 기업 리스트 생성 메서드(더미 & 실제)
    public List<Company> createCompanyList() {
        return List.of(
                Company.builder().ticker("AAPL").name("Apple Inc.").sector("Technology").exchange("NASDAQ").build(), // api로 불러올 실제 기업
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

    // 기업 생성 헬퍼 메서드
    private Company createCompany(String ticker, String name, String sector, String exchange) {
        return Company.builder()
                .ticker(ticker)
                .name(name)
                .sector(sector)
                .exchange(exchange)
                .build();
    }

    // 더미 기업들의 재무제표 생성 메서드
    public List<FinancialStatement> generateFinancials(Company company) {
        SectorProfile profile = SectorProfile.findBySector(company.getSector());
        List<FinancialStatement> list = new ArrayList<>();

        // 랜덤으로 기업 고유 역량 부여: 0.5(부실) ~ 1.5(우량)
        double companyQuality = 0.5 + random.nextDouble(); // 값이 높을수록 이익률은 높고 부채비율은 낮아짐

        // 초기 연 매출 (섹터별 기본값 + 랜덤 변동)
        double currentAnnualRevenue = profile.getBaseRevenue() * (0.8 + random.nextDouble() * 0.4);

        for(int i = 0; i < YEARS_OF_DATA * 4; i++) { // 3년 * 4분기 = 12번 반복
            int year = START_DATE.getYear() + (i / 4);
            int quarter = 1 + (i % 4);

            // --- 매출 계산 ---
            // 매출이 매년 성장한다고 가정 (분기별 성장 + 섹터별 성장률 + 기업 역량 반영)
            currentAnnualRevenue = applyGrowth(currentAnnualRevenue, profile, companyQuality);
            BigDecimal revenue = calculateRevenue(currentAnnualRevenue, quarter); // 계절성 반영해 매출 산출

            // --- 이익 및 비용 계산 ---
            BigDecimal operatingProfit = calculateOperatingProfit(revenue, profile, companyQuality, company.getName(), year, quarter);
            BigDecimal netIncome = operatingProfit.multiply(BigDecimal.valueOf(0.75)); // 법인세 차감한 순이익

            // --- 재무상태표 계산  ---
            // 자산 회전율을 통해 자산 규모 추정 (Asset Turnover)
            BigDecimal totalAssets = revenue.multiply(BigDecimal.valueOf(4.0)).multiply(BigDecimal.valueOf(profile.getAssetTurnoverMultiplier()));
            BigDecimal totalLiabilities = calculateLiabilities(totalAssets, profile, companyQuality);
            BigDecimal totalEquity = totalAssets.subtract(totalLiabilities); // 자본 = 자산 - 부채

            // --- 현금흐름 및 투자 계산 --
            BigDecimal operatingCashFlow = operatingProfit.multiply(BigDecimal.valueOf(1.1)); // 일반적으로 이익보다 현금흐름이 큼
            BigDecimal rnd = revenue.multiply(BigDecimal.valueOf(profile.getRndRatio()));
            BigDecimal capex = revenue.multiply(BigDecimal.valueOf(profile.getCapexRatio()));

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

    // 더미 기업들의 주가 생성 메서드
    public List<StockPriceHistory> generateStockPrices(Company company, List<FinancialStatement> financials) {
        SectorProfile profile = SectorProfile.findBySector(company.getSector());
        List<StockPriceHistory> history = new ArrayList<>();
        double currentPrice = calculateInitialPrice(financials.get(0), profile); // 초기 주가 설정
        LocalDate currentDate = START_DATE;
        int financialIndex = 0;

        while(!currentDate.isAfter(END_DATE)) {
            if(isTradingDay(currentDate)) {
                // 현재 시점에 적용되는 가장 최근 분기 실적 찾기
                financialIndex = updateFinancialIndex(financials, financialIndex, currentDate);
                FinancialStatement currentFS = financials.get(financialIndex);
                // 적정 주가 및 변동성 계산
                double targetPrice = calculateFairValue(currentFS, profile);
                currentPrice = calculateNextPrice(currentPrice, targetPrice, profile);

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

    // --- 헬퍼 메서드 ---
    private double applyGrowth(double currentRevenue, SectorProfile profile, double quality) {
        double growthFactor = 1 + (profile.getGrowthRate() / 4.0 * quality) + (random.nextGaussian() * 0.02);
        return currentRevenue * growthFactor;
    }

    // 계절성 반영(4분기에 매출 증가 등)
    private BigDecimal calculateRevenue(double currentAnnualRevenue, int quarter) {
        double seasonalFactor = (quarter == 4) ? 1.1 : 0.97;
        return BigDecimal.valueOf(currentAnnualRevenue / 4.0 * seasonalFactor)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateOperatingProfit(BigDecimal revenue, SectorProfile profile, double quality, String companyName, int year, int quarter) {
        double adjustedMargin = profile.getOperatingMargin() * quality; // 기업 역량이 좋을수록 마진이 높음.
        double marginNoise = random.nextGaussian() * 0.05; // 마진 변동 폭 (약 5% 표준편차)
        BigDecimal profit = revenue.multiply(BigDecimal.valueOf(adjustedMargin + marginNoise));

        // 어닝 쇼크 구현 (10% 확률로 이익 급감 혹은 적자 전환)
        if(random.nextDouble() < 0.1) {
            profit = profit.multiply(BigDecimal.valueOf(-0.5));
            log.debug("[InitData] {} - 어닝 쇼크 발생 (Year: {}, Q: {})", companyName, year, quarter);
        }
        return profit;
    }

    private BigDecimal calculateLiabilities(BigDecimal totalAssets, SectorProfile profile, double quality) {
        double debtMultiplier = 2.0 - quality; // 기업 역량이 높을수록 부채비율 낮음
        return totalAssets.multiply(BigDecimal.valueOf(profile.getDebtRatio() * debtMultiplier));
    }

    private double calculateInitialPrice(FinancialStatement initialFS, SectorProfile profile) {
        // EPS 계산: 연간 환산 순이익 / 주식 수
        // 연간 환산 순이익 = 분기 순이익 * 4
        double annualEPS = initialFS.getNetIncome().doubleValue() * 4 / (double) TOTAL_SHARES;
        return Math.max(5.0, annualEPS * profile.getPeRatio()); // 최소 $5 보장
    }

    private boolean isTradingDay(LocalDate date) {
        return date.getDayOfWeek().getValue() <= 5;  // 주말 제외
    }

    private int updateFinancialIndex(List<FinancialStatement> financials, int currentIndex, LocalDate currentDate) {
        // 다음 분기 실적 날짜(가정)를 지났다면 인덱스 증가
        if (currentIndex < financials.size() - 1) {
            LocalDate nextQuarterDate = getQuarterEndDate(financials.get(currentIndex + 1));
            if (currentDate.isAfter(nextQuarterDate)) {
                return currentIndex + 1;
            }
        }
        return currentIndex;
    }

    // 적정 주가(Fair Value): (최근 분기 순이익 * 4) / 1억주 * PER
    private double calculateFairValue(FinancialStatement fs, SectorProfile profile) {
        return (fs.getNetIncome().doubleValue() * 4 / (double) TOTAL_SHARES) * profile.getPeRatio();
    }

    private double calculateNextPrice(double currentPrice, double targetPrice, SectorProfile profile) {
        // Drift: 적정 주가로 수렴하려는 힘 (Mean Reversion)
        double drift = (targetPrice - currentPrice) * 0.005; // 천천히 수렴함
        // Volatility: 랜덤 등락 (섹터별 변동성 + 시장 노이즈)
        double shock = currentPrice * random.nextGaussian() * (profile.getVolatility() / Math.sqrt(252));
        double nextPrice = currentPrice + drift + shock;
        return Math.max(1.0, nextPrice); // 동전주 방지를 위해 최소 1달러
    }

    // 분기 종료일 헬퍼 메서드
    private LocalDate getQuarterEndDate(FinancialStatement fs) {
        int month = fs.getQuarter() * 3;
        // 해당 년도 해당 분기의 마지막 날 대략 계산 (3/31, 6/30...)
        return LocalDate.of(fs.getYear(), month, 1).plusMonths(1).minusDays(1);
    }


}
