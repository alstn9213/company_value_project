import { useMemo } from "react";
import { MacroDataResponse } from "../../../types/macro";

export interface InversionInterval {
  start: string;
  end: string;
}

//  금리 역전 구간 계산 로직
const calculateInversionIntervals = (data: MacroDataResponse[]): InversionInterval[] => {
  const INVERSION_THRESHOLD = 0;

  if (!data || data.length === 0) {
    return [];
  }

  const intervals: InversionInterval[] = [];
  let currentStart: string | null = null;

  for (let i = 0; i < data.length; i++) {
    const { spread, date } = data[i];
    const isInverted = spread < INVERSION_THRESHOLD;

    // 역전 시작 감지
    if (isInverted && !currentStart) {
      currentStart = date;
    }
    
    // 역전 종료 감지 (이전 데이터가 역전의 끝)
    else if (!isInverted && currentStart) {
      intervals.push({
        start: currentStart,
        end: data[i - 1].date, // 직전 데이터의 날짜가 구간의 끝
      });
      currentStart = null;
    }
  }

  // 마지막 데이터까지 역전 상태가 지속된 경우 처리
  if (currentStart) {
    intervals.push({
      start: currentStart,
      end: data[data.length - 1].date,
    });
  }

  return intervals;
};

export const useInversionIntervals = (history: MacroDataResponse[] | undefined): InversionInterval[] => {
  return useMemo(() => {
    return calculateInversionIntervals(history || []);
  }, [history]);
};
