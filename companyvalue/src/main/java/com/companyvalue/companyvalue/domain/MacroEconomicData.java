package com.companyvalue.companyvalue.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MacroEconomicData {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "macro_id")
    private Long id;

    @Column(unique = true)
    private LocalDate recordedDate; // 기준 일자

    // --- FRED 데이터 ---
    private Double fedFundsRate;     // 기준 금리
    private Double us10yTreasuryYield; // 미 10년물 국채 금리 (채권 매력도 판단)
    private Double us2yTreasuryYield;  // 미 2년물 국채 금리 (장단기 금리차 계산용)

    private Double inflationRate;    // 인플레이션 (CPI)
    private Double unemploymentRate; // 실업률
}