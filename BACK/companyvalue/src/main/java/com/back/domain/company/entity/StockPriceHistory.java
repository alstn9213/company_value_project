package com.back.domain.company.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "stock_price_history",
        indexes = @Index(name = "idx_company_date", columnList = "company_id, recorded_date") // 조회 성능 향상
)
public class StockPriceHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id")
  private Company company;

  private LocalDate recordedDate;

  private BigDecimal closePrice;  // 종가

  @Builder
  public StockPriceHistory(Company company, LocalDate recordedDate, BigDecimal closePrice) {
    this.company = company;
    this.recordedDate = recordedDate;
    this.closePrice = closePrice;
  }
}