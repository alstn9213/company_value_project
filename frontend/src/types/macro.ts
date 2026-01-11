export interface MacroData {
  date: string;
  fedFundsRate: number; // 기준금리
  us10y: number; // 10년물 국채
  us2y: number; // 2년물 국채
  spread: number; // 장단기 금리차 (10y - 2y)
  inflation: number; // 인플레이션 (CPI)
  unemployment: number; // 실업률
}
