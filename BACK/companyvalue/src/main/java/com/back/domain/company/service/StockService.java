package com.back.domain.company.service;

import com.back.domain.company.dto.response.StockHistoryResponse;
import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.StockPriceHistory;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.company.repository.StockPriceHistoryRepository;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

  private final StockPriceHistoryRepository stockRepository;
  private final CompanyRepository companyRepository;

  // DB에서 주가 데이터를 가져오는 메서드.
  // 주가 차트 그리기 용
  @Cacheable(value = "stock_history", key = "#ticker", unless = "#result == null || #result.isEmpty()")
  public List<StockHistoryResponse> getStockHistory(String ticker) {
    Company company = companyRepository.findByTicker(ticker)
            .orElseThrow(()-> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

    // DB 조회 (데이터가 없으면 빈 리스트 반환)
    // 데이터의 존재 유무는 'SeedDataLoader'와 'Scheduler'가 책임진다
    List<StockPriceHistory> histories = stockRepository.findByCompanyOrderByRecordedDateAsc(company);

    // Entity -> DTO 변환하여 반환 (이 결과가 Redis에 저장됨)
    return histories.stream()
            .map(h -> new StockHistoryResponse(h.getRecordedDate(), h.getClosePrice()))
            .toList();
  }


}
