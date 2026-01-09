package com.back.domain.company.service.analysis.validator;

import com.back.domain.company.entity.FinancialStatement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Component
public class FinancialDataValidator {

  /**
   * 필요한 재무 데이터 필드들이 모두 존재하는지 검증합니다.
   * 하나라도 누락되면 경고 로그를 남기고 false를 반환합니다.
   *
   * @param fs 재무제표 엔티티
   * @param getters 검증할 필드를 가져올 메서드 참조 (예: FinancialStatement::getNetIncome)
   * @return 모든 데이터가 존재하면 true, 아니면 false
   */
  @SafeVarargs
  public final boolean hasRequiredFields(FinancialStatement fs, Function<FinancialStatement, BigDecimal>... getters) {
    if (fs == null) {
      log.warn("[데이터 누락] 재무제표 객체(FinancialStatement)가 null입니다.");
      return false;
    }

    boolean hasAllData = Arrays.stream(getters)
            .map(getter -> getter.apply(fs))
            .allMatch(Objects::nonNull);

    if (!hasAllData) {
      String companyName = (fs.getCompany() != null) ? fs.getCompany().getName() : "Unknown";
      log.warn("[데이터 누락] {}: 필수 재무 데이터가 일부 누락되었습니다.", companyName);
    }

    return hasAllData;
  }
}