package com.back.infra.scheduler;

import com.back.domain.company.entity.Company;
import com.back.domain.company.event.CompanyFinancialsUpdatedEvent;
import com.back.domain.company.repository.CompanyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScoringTestController {

    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 서버 실행 시 이 로그가 찍히는지 반드시 확인하세요.
     * 안 찍힌다면 파일 위치가 잘못되었거나 빌드가 안 된 것입니다.
     */
    @PostConstruct
    public void init() {
        log.info("=============================================");
        log.info(">>> [Test] ScoringTestController가 정상적으로 로드되었습니다.");
        log.info(">>> 테스트 URL: http://localhost:8080/api/test/re-score");
        log.info("=============================================");
    }

    /**
     * 브라우저에서 바로 실행할 수 있도록 @GetMapping으로 변경했습니다.
     * URL: http://localhost:8080/test/re-score
     */
    @GetMapping("/test/re-score")
    public String forceReCalculateScores() {
        List<Company> companies = companyRepository.findAll();
        log.info(">>> [Test] 수동 점수 재계산 요청 받음! 대상 기업 수: {}개", companies.size());

        if (companies.isEmpty()) {
            return "오류: DB에 저장된 기업이 하나도 없습니다. 기업 데이터부터 넣어야 합니다.";
        }

        int count = 0;
        for (Company company : companies) {
            // 이벤트를 발행하여 리스너(CompanyEventListener)를 깨웁니다.
            eventPublisher.publishEvent(new CompanyFinancialsUpdatedEvent(company.getTicker()));
            count++;
        }

        return String.format("재계산 요청 완료! %d개 기업에 대해 이벤트를 발행했습니다. 서버 로그를 확인하세요.", count);
    }
}