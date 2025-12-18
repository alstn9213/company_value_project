package com.back.domain.company.service.core;

import com.back.domain.company.dto.response.CompanyDetailResponse;
import com.back.domain.company.dto.response.CompanySummaryResponse;
import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.entity.FinancialStatement;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
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

    @Transactional(readOnly = true)
    public Page<CompanySummaryResponse> getAllCompanies(int page, int size, String sort) {
        Sort sortObj = Sort.by(Sort.Direction.ASC, "name"); // 기본: 이름순
        if("score".equals(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "companyScore.totalScore");
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        return companyRepository.findAll(pageable)
                .map(CompanySummaryResponse::from);
    }

    public List<CompanySummaryResponse> searchCompanies(String keyword) {
        return companyRepository.findTop10ByTickerContainingIgnoreCaseOrNameContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(CompanySummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyDetailResponse getCompanyDetail(String ticker) {
        Company company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new IllegalArgumentException("..."));

        CompanyScore score = companyScoreRepository.findByCompany(company)
                .orElse(CompanyScore.empty(company));

        List<FinancialStatement> history = financialStatementRepository.findByCompanyOrderByYearDescQuarterDesc(company);

        return CompanyDetailResponse.of(company, score, history);
    }
}
