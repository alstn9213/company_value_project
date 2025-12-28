package com.back.infra.scheduler.controller;

import com.back.domain.company.entity.Company;
import com.back.domain.company.event.CompanyFinancialsUpdatedEvent;
import com.back.domain.company.repository.CompanyRepository;
import com.back.infra.scheduler.service.CompanyBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test/company")
@RequiredArgsConstructor
public class CompanyTestController {

    private final CompanyBatchService companyBatchService;
    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 모든 기업의 재무 정보 업데이트 (API 호출)
     * URL: http://localhost:8080/test/company/schedule/run
     */
    @GetMapping("/schedule/run")
    public String runScheduleManually() {
        log.info(">>> [Test] 모든 기업 데이터 수동 업데이트 요청");
        companyBatchService.executeAllCompaniesUpdate();
        return "모든 기업 업데이트 요청 완료 (로그 확인 필요).";
    }

    /**
     * 저장된 데이터를 기반으로 점수만 다시 계산
     * URL: http://localhost:8080/test/company/re-score
     */
    @GetMapping("/re-score")
    public String forceReCalculateScores() {
        List<Company> companies = companyRepository.findAll();
        log.info(">>> [Test] 수동 점수 재계산 요청 받음! 대상 기업 수: {}개", companies.size());

        if(companies.isEmpty()) {
            return "오류: DB에 저장된 기업이 하나도 없습니다. 기업 데이터부터 넣어야 합니다.";
        }

        int count = 0;
        for(Company company : companies) {
            eventPublisher.publishEvent(new CompanyFinancialsUpdatedEvent(company.getTicker()));
            count++;
        }

        return String.format("재계산 요청 완료! %d개 기업에 대해 이벤트를 발행했습니다. 서버 로그를 확인하세요.", count);
    }
}