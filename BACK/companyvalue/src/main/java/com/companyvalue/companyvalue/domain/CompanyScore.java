package com.companyvalue.companyvalue.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyScore extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private Integer totalScore; // 최종 점수 (0~100)

    // --- 세부 항목 점수 ---
    private Integer stabilityScore;    // 안정성
    private Integer profitabilityScore;// 수익성
    private Integer valuationScore;    // 가치
    private Integer investmentScore;   // 미래 투자 (가산점/감점 반영)

    private String grade; // 등급 (S, A, B, F 등)

    // 생성자 및 비즈니스 로직(점수 업데이트 메서드) 등...
}