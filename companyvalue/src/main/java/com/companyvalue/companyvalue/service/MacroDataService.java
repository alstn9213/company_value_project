package com.companyvalue.companyvalue.service;

import com.companyvalue.companyvalue.repository.MacroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MacroDataService {

    private final MacroRepository macroRepository;
    // private final FredApiClient fredApiClient; // (가칭) HTTP 클라이언트

    @Transactional
    public void updateMacroEconomicData() {
        // 1. 외부 API (FRED) 호출
        // Double interestRate = fredApiClient.getInterestRate();
        // Double us10y = fredApiClient.get10YearYield();
        // ...

        // 2. Entity 생성 및 저장
        // MacroEconomicData data = MacroEconomicData.builder()
        //      .fedFundsRate(interestRate)
        //      .us10yTreasuryYield(us10y)
        //      .recordedDate(LocalDate.now())
        //      .build();

        // macroRepository.save(data);
    }
}