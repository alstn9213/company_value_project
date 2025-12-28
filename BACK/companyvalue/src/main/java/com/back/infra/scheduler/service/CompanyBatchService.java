package com.back.infra.scheduler.service;

import com.back.domain.company.entity.Company;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.CompanyScoreRepository;
import com.back.domain.company.repository.FinancialStatementRepository;
import com.back.domain.company.service.finance.FinancialDataSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyBatchService {

    private final CompanyRepository companyRepository;
    private final FinancialDataSyncService financialDataSyncService;
    private final FinancialStatementRepository financialStatementRepository;
    private final CompanyScoreRepository companyScoreRepository;

    // 전체 기업 정보 최신화 메서드
    public void executeAllCompaniesUpdate() {
        List<Company> companies = companyRepository.findAll();
        log.info(">>> [Batch] 전체 기업 업데이트 시작 - 대상 기업 수: {}개", companies.size());
        for(Company company : companies) {
            String ticker = company.getTicker();
            // 최신 데이터 여부 확인
            if(isDataUpToDate(company)) {
                log.debug(">>> [Batch] 이미 최신 데이터가 존재하여 스킵: {}", ticker);
                continue;
            }
            // 데이터 동기화
            processCompanyUpdate(ticker);
        }
        log.info(">>> [Batch] 전체 기업 업데이트 완료");
    }


    // --- 헬퍼 메서드 ---
    // 해당 기업의 재무제표와 점수가 이미 최신 상태인지 확인하는 헬퍼 메서드
    private boolean isDataUpToDate(Company company) {
        boolean hasFinancials = financialStatementRepository
                .findTopByCompanyOrderByYearDescQuarterDesc(company)
                .isPresent();

        boolean hasScore = companyScoreRepository.findByCompany(company).isPresent();

        return hasFinancials && hasScore;
    }

    // 개별 기업에 대한 업데이트 로직과 예외 처리를 담당하는 헬퍼 메서드
    private void processCompanyUpdate(String ticker) {
        try {
            // 트랜잭션 실행 (데이터 동기화)
            financialDataSyncService.synchronizeCompany(ticker);

            // api 제한 조절 (트랜잭션 밖에서 대기)
            Thread.sleep(12000);

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("배치 작업 중 인터럽트 발생", ie);
        } catch (Exception e) {
            log.error("[Batch] {} 업데이트 실패: {}", ticker, e.getMessage());
        }
    }



}
