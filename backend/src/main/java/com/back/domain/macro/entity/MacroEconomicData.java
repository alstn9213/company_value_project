package com.back.domain.macro.entity;

import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

  @Builder
  public MacroEconomicData(LocalDate recordedDate,
                           Double fedFundsRate,
                           Double us10yTreasuryYield,
                           Double us2yTreasuryYield,
                           Double inflationRate,
                           Double unemploymentRate) {

    this.recordedDate = recordedDate;
    this.fedFundsRate = fedFundsRate;
    this.us10yTreasuryYield = us10yTreasuryYield;
    this.us2yTreasuryYield = us2yTreasuryYield;
    this.inflationRate = inflationRate;
    this.unemploymentRate = unemploymentRate;
  }

  // 데이터 업데이트 메서드 (이미 오늘 날짜 데이터가 있을 경우 덮어쓰기 위함)
  public void updateData(Double fedFundsRate,
                         Double us10yTreasuryYield,
                         Double us2yTreasuryYield,
                         Double inflationRate,
                         Double unemploymentRate
  ) {
    if (unemploymentRate != null && unemploymentRate < 0) {
      throw new BusinessException(ErrorCode.INVALID_MACRO_VALUE);
    }

    this.fedFundsRate = fedFundsRate;
    this.us10yTreasuryYield = us10yTreasuryYield;
    this.us2yTreasuryYield = us2yTreasuryYield;
    this.inflationRate = inflationRate;
    this.unemploymentRate = unemploymentRate;
  }
}