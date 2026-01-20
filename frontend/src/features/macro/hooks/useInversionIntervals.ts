import { useMemo } from "react";
import { MacroDataResponse } from "../../../types/macro";

export const useInversionIntervals = (history: MacroDataResponse[] | undefined) => {
  return useMemo(() => {
    const data = history || [];

    if (data.length === 0) {
      return [];
    }

    const INVERSION_THRESHOLD = 0;
    const intervals: { start: string; end: string }[] = [];
    let currentStart: string | null = null;

    for (let i = 0; i < data.length; i++) {
      const { spread, date } = data[i];
      // 장단기 금리 차이(spread = us10 - us2)가 0보다 작다면, 장단기 금리 역전 상황임
      const isInverted = spread < INVERSION_THRESHOLD;

      if (isInverted && !currentStart) {
        currentStart = date;
      } else if (!isInverted && currentStart) {
        intervals.push({
          start: currentStart,
          end: data[i - 1].date, // 직전 데이터가 역전 구간의 끝
        });
        currentStart = null; // 구간 초기화
      }
    }

    // 마지막 데이터까지 역전이 지속된 경우 처리
    if (currentStart) {
      intervals.push({
        start: currentStart,
        end: data[data.length - 1].date,
      });
    }

    return intervals;
  }, [history]);
};