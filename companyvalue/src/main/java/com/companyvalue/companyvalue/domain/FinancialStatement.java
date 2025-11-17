package com.companyvalue.companyvalue.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private Integer year;    // 연도
    private Integer quarter; // 분기 (1, 2, 3, 4)

    // 금액이 매우 크므로 BigDecimal 사용
    private BigDecimal revenue;           // 매출액
    private BigDecimal operatingProfit;   // 영업이익
    private BigDecimal netIncome;         // 당기순이익

    private BigDecimal totalAssets;       // 자산총계
    private BigDecimal totalLiabilities;  // 부채총계
    private BigDecimal totalEquity;       // 자본총계

    private BigDecimal operatingCashFlow; // 영업활동 현금흐름

    // --- 투자 적극성 평가를 위한 필드 ---
    private BigDecimal researchAndDevelopment; // R&D 비용
    private BigDecimal capitalExpenditure;     // CapEx (자본지출)


}