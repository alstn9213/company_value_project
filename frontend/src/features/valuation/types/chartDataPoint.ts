export interface ChartDataPoint {
  subject: string;
  score: number;
  fullMark: number;
  normalizedScore?: number; // 내부 계산용
}