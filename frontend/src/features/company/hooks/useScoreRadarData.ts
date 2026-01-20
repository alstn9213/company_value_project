import { useMemo } from 'react';
import { ChartDataPoint } from '../types/chartDataPoint';

interface UseScoreRadarDataResult {
  normalizedData: (ChartDataPoint & { normalizedScore: number })[];
  hasData: boolean;
}

export const useScoreRadarData = (data: ChartDataPoint[] | undefined): UseScoreRadarDataResult => {
  const normalizedData = useMemo(() => {
    if (!data || data.length === 0) {
      return [];
    }

    // 그래프용 환산 점수: (내점수 / 만점) * 100
    return data.map((item) => ({
      ...item,
      // fullMark가 0이거나 없을 경우를 대비한 100
      normalizedScore: (item.score / (item.fullMark || 100)) * 100,
    }));
  }, [data]);

  return {
    normalizedData,
    hasData: normalizedData.length > 0,
  };
};