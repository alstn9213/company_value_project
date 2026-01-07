package com.back.domain.company.service;

import com.back.domain.company.dto.response.CompanyDetailResponse;
import com.back.domain.company.dto.response.CompanySummaryResponse;
import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyReadService {

  private final CompanyRepository companyRepository;
  private final CompanyScoreRepository companyScoreRepository;
  private final FinancialStatementRepository financialStatementRepository;

  // 기업 목록 페이지에 모든 기업들을 나열하는 메서드
  public Page<CompanySummaryResponse> getAllCompanies(int page, int size, String sort) {
    // 점수 순으로 기업 나열하는 것이 기본값
    Sort sortObj = Sort.by(Sort.Direction.DESC, "companyScore.totalScore");

    // 이름순을 선택하면 이름 순으로 정렬
    if ("name".equals(sort)) {
      sortObj = Sort.by(Sort.Direction.ASC, "name");
    }

    Pageable pageable = PageRequest.of(page, size, sortObj);

    return companyRepository.findAll(pageable)
            .map(CompanySummaryResponse::from);
  }

  // 기업의 상세 정보를 가져오는 메서드
  public CompanyDetailResponse getCompanyDetail(String ticker) {
    Company company = companyRepository.findByTicker(ticker)
            .orElseThrow(()-> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

    CompanyScore score = companyScoreRepository.findByCompany(company)
            .orElse(CompanyScore.empty(company));

    List<FinancialStatement> history = financialStatementRepository.findByCompanyOrderByYearDescQuarterDesc(company);

    return CompanyDetailResponse.of(company, score, history);
  }

  // top 10 회사 나열용 메서드
  public List<CompanySummaryResponse> searchCompanies(String keyword) {
    return companyRepository.findTop10ByTickerContainingIgnoreCaseOrNameContainingIgnoreCase(keyword, keyword)
            .stream()
            .map(CompanySummaryResponse::from)
            .toList();
  }


}
