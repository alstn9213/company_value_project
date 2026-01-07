package com.back.domain.macro.service;

import com.back.domain.macro.entity.MacroEconomicData;
import com.back.domain.macro.entity.MacroIndicator;
import com.back.domain.macro.repository.MacroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MacroDataService {

  private final MacroRepository macroRepository;
  private final MacroDataProvider macroDataProvider;

  // 거시 경제 데이터 최신화 및 저장까지 하는 메서드
  @Transactional
  @CacheEvict(value = {"macro_latest", "macro_history"}, allEntries = true)
  public void updateMacroEconomicData() {
    log.info("거시 경제 정보 업데이트 시작...");
    //  --- 외부 API에서 최신 데이터 가져오기 ---
    Map<MacroIndicator, Double> latestValues = new EnumMap<>(MacroIndicator.class);
    for(MacroIndicator indicator : MacroIndicator.values()) {
      // --- 각 지표(키)마다 최신 값 넣기 ---
      macroDataProvider.fetchLatestValue(indicator)
              .ifPresent(value -> latestValues.put(indicator, value));
    }

    // 실업률이나 인플레는 달마다 한 번 발표되지만, 금리는 매일 갱신 된다.
    // 그래서 결측치(실업률, 인플레)를 보정해서 DB에 저장하는 메서드를 따로 정의했다.
    saveLatestData(latestValues);
    log.info("업데이트 및 캐시 초기화 완료");
  }

  // 지표들을 DB에 저장하고 결측치는 보정하는 헬퍼
  private void saveLatestData(Map<MacroIndicator, Double> newValues) {
    LocalDate today = LocalDate.now();
    // --- 결측치 보정 ---
    // DB에 저장된 직전 데이터를 가져와서
    MacroEconomicData lastEntity = macroRepository.findTopByOrderByRecordedDateDesc().orElse(null);
    // Map으로 변환 (putAll을 사용하기 위해)
    Map<MacroIndicator, Double> currentValues = convertToMap(lastEntity);
    // 새 데이터(금리)를 덮어씌움
    currentValues.putAll(newValues); // 기존 값(CPI 등)은 유지되고, 새로운 값(금리)은 갱신됨

    // --- DB에 저장 ---
    // DB에서 지금까지 저장된 데이터 가져오기
    MacroEconomicData macroData = macroRepository.findByRecordedDate(today)
            .orElseGet(() -> MacroEconomicData.builder()
                    .recordedDate(today)
                    .build()
            );
    // currentValues로 업데이트
    macroData.updateData(
            currentValues.get(MacroIndicator.FED_FUNDS),
            currentValues.get(MacroIndicator.US_10Y),
            currentValues.get(MacroIndicator.US_2Y),
            currentValues.get(MacroIndicator.CPI),
            currentValues.get(MacroIndicator.UNEMPLOYMENT)
    );
    // DB에 저장
    macroRepository.save(macroData);
  }


  // 과거 거시경제 데이터 일괄 초기화 메서드
  // 맨 처음 DB를 구축할 때 사용(이후엔 위에 정의된 업데이트 메서드 사용)
  @Transactional
  public void initHistoricalMacroData() {
    if(macroRepository.count() > 0) {
      log.info("이미 거시 경제 데이터가 존재하여 초기화를 건너뜁니다.");
      return;
    }

    log.info("거시 경제 과거 데이터 수집 시작 (DB를 구축해야하는 최초 1회에만 실행)");

    // --- 데이터 수집 및 병합 ---
    // 데이터 수집
    Map<LocalDate, Map<MacroIndicator, Double>> combinedData = fetchAndMergeAllHistory();
    // 날짜 오름차순 정렬 (과거 -> 현재 순으로 처리해야 빈 값을 직전 값으로 채울 수 있음)
    List<LocalDate> sortedDates = combinedData.keySet().stream().sorted().toList();
    // 날짜 리스트와 지표 데이터들을 합쳐 엔티티로 변환
    List<MacroEconomicData> dataList = convertToEntities(sortedDates, combinedData);

    macroRepository.saveAll(dataList);
    log.info("초기화 완료: 총 {}건 저장됨", dataList.size());
  }

  // Api를 호출해서(fetchHistory) 데이터를 받아오고
  // forEach로 날짜와 지표 데이터를 병합해서
  // Map으로 반환하는 헬퍼
  private Map<LocalDate, Map<MacroIndicator, Double>> fetchAndMergeAllHistory() {
    Map<LocalDate, Map<MacroIndicator, Double>> historyMap = new HashMap<>();
    for(MacroIndicator indicator : MacroIndicator.values()) {
      // api에서 날짜(키)와 날짜별 지표 값(값)들을 가져옴
      Map<LocalDate, Double> indicatorData = macroDataProvider.fetchHistory(indicator);
      // 받아온 데이터를 메인 Map에 병합
      indicatorData.forEach((date, value) ->
              historyMap.computeIfAbsent(date, k -> new EnumMap<>(MacroIndicator.class))
                      .put(indicator, value)
      );
    }
    return historyMap;
  }


  // --- 단순 헬퍼 메서드 ---

  // 직전 데이터를 Map으로 다루기 위해 엔티티를 Map으로 변환하는 헬퍼 메서드
  private Map<MacroIndicator, Double> convertToMap(MacroEconomicData entity) {
    Map<MacroIndicator, Double> map = new EnumMap<>(MacroIndicator.class);
    if(entity == null) return map; // null이면 빈 맵 반환

    // Enum과 Entity 필드 매핑
    map.put(MacroIndicator.FED_FUNDS, entity.getFedFundsRate());
    map.put(MacroIndicator.US_10Y, entity.getUs10yTreasuryYield());
    map.put(MacroIndicator.US_2Y, entity.getUs2yTreasuryYield());
    map.put(MacroIndicator.CPI, entity.getInflationRate());
    map.put(MacroIndicator.UNEMPLOYMENT, entity.getUnemploymentRate());

    // 값이 null인 키는 맵에서 제거
    // FRED가 나중에 새로운 지표(비트코인)을 추가하면
    // 추가한 날짜 이전의 비트코인 지표값들은 null이 되니 그걸 방지하기 위한 안전장치
    map.values().removeIf(Objects::isNull);

    return map;
  }

  // 최신화된 Map을 DB에 저장하기 위해 다시 엔티티로 변환
  private List<MacroEconomicData> convertToEntities(List<LocalDate> dates, Map<LocalDate, Map<MacroIndicator, Double>> combinedData) {
    List<MacroEconomicData> result = new ArrayList<>();

    // "직전 값"을 기억할 저장소 (매일매일 갱신됨)
    // 초기값은 null로 시작
    Map<MacroIndicator, Double> runningValues = new EnumMap<>(MacroIndicator.class);

    for(LocalDate date : dates) {
      Map<MacroIndicator, Double> newValues = combinedData.get(date);

      // 직전 값(runningValues)에 새 값(newValues)을 덮어씌움
      // putAll을 쓰면:
      // - 새 값이 있으면 교체됨 (Daily 지표 갱신)
      // - 새 값이 없으면 기존 값 유지 (Monthly 지표 유지)
      // - 기존 값도 없고 새 값도 없으면 그냥 없음 (최초 데이터 등)
      if(newValues != null) runningValues.putAll(newValues);

      // runningValues로 엔티티 생성
      MacroEconomicData entity = MacroEconomicData.builder()
              .recordedDate(date)
              .fedFundsRate(runningValues.get(MacroIndicator.FED_FUNDS))
              .us10yTreasuryYield(runningValues.get(MacroIndicator.US_10Y))
              .us2yTreasuryYield(runningValues.get(MacroIndicator.US_2Y))
              .inflationRate(runningValues.get(MacroIndicator.CPI))
              .unemploymentRate(runningValues.get(MacroIndicator.UNEMPLOYMENT))
              .build();

      result.add(entity);
    }
    return result;
  }






}
