package com.back.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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
    private Boolean isOpportunity; // 저점 매수 기회 여부

    public void updateScore(Integer totalScore,
                            Integer stabilityScore,
                            Integer profitabilityScore,
                            Integer valuationScore,
                            Integer investmentScore,
                            String grade,
                            Boolean isOpportunity) {
        this.totalScore = totalScore;
        this.stabilityScore = stabilityScore;
        this.profitabilityScore = profitabilityScore;
        this.valuationScore = valuationScore;
        this.investmentScore = investmentScore;
        this.grade = grade;
        this.isOpportunity = isOpportunity;
    }

    @Builder
    public CompanyScore(Company company,
                        Integer totalScore,
                        String grade,
                        Boolean isOpportunity) {
        this.company = company;
        this.totalScore = totalScore;
        this.grade = grade;
        this.isOpportunity = isOpportunity;
    }
}