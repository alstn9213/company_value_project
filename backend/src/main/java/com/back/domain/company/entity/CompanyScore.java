package com.back.domain.company.entity;

import com.back.domain.company.service.analysis.dto.ScoreEvaluationResultDto;
import com.back.domain.time.BaseTime;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    private Integer investmentScore;   // 미래 투자 (가산점, 감점 반영)
    private String grade; // 등급 (S, A, B, F 등)
    private Boolean isOpportunity; // 저점 매수 기회 여부

    @Builder
    public CompanyScore(
            Company company,
            Integer totalScore,
            String grade,
            Boolean isOpportunity
    ) {
        this.company = company;
        this.totalScore = totalScore;
        this.grade = grade;
        this.isOpportunity = isOpportunity;
    }

  // 점수 최신화 메서드
  // 원래는 계층 분리때문에 엔티티에 DTO클래스를 import해오는 건 권장하지 않으나
  // 프로젝트 규모가 작아서 이 메서드를 일단 엔티티에 정의해둠
  // 나중에 이 메서드를 분리하고 싶으면 따로 DTO를 만들자.
  public void updateScore(ScoreEvaluationResultDto result) {
    this.totalScore = result.totalScore();
    this.stabilityScore = result.stability();
    this.profitabilityScore = result.profitability();
    this.valuationScore = result.valuation();
    this.investmentScore = result.investment();
    this.grade = result.grade();
    this.isOpportunity = result.isOpportunity();
  }

}