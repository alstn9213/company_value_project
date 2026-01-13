import { useMemo } from "react";
import { MacroDataResponse } from "../../../types/macro";

interface Interval {
  start: string;
  end: string;
}

/**
 * 거시경제 데이터 기록에서 장단기 금리차 역전 구간을 계산하는 훅
 * @param history MacroData 배열
 * @returns 역전 구간(start, end date) 배열
 */

export const useInversionIntervals = (history?: MacroDataResponse[]): Interval[] => {
  return useMemo(() => {
    if (!history || history.length === 0) return [];

    const intervals: Interval[] = [];
    let startTime: string | null = null;

    history.forEach((d, index) => {
      // spread(장단기 금리차)가 0보다 작으면 역전 상태
      const isInverted = d.spread < 0;

      if (isInverted && !startTime) {
        // 역전 시작
        startTime = d.date;
      } else if (!isInverted && startTime) {
        // 역전 종료 (이전 데이터까지가 역전 구간)
        intervals.push({
          start: startTime,
          end: history[index - 1].date,
        });
        startTime = null;
      }
    });

    // 마지막 데이터까지 역전 상태가 지속된 경우 처리
    if (startTime) {
      intervals.push({
        start: startTime,
        end: history[history.length - 1].date,
      });
    }
    return intervals;
  }, [history]);
};