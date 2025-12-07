package com.back.domain.company.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String ticker; // 예: AAPL, TSLA

    @Column(nullable = false)
    private String name; // 예: Apple Inc.

    private String sector; // 예: Technology

    private String exchange; // 예: NASDAQ, NYSE (미국 주식 구분)

    @OneToOne(mappedBy = "company", fetch = FetchType.LAZY)
    private CompanyScore companyScore;

    @Builder
    public Company(String ticker,
                   String name,
                   String sector,
                   String exchange,
                   CompanyScore companyScore
    ) {
        this.ticker = ticker;
        this.name = name;
        this.sector = sector;
        this.exchange = exchange;
        this.companyScore = companyScore;
    }
}
