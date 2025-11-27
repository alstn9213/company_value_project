export interface Company {
  ticker: string;
  name: string;
  sector: string;
  exchange: string;
}

// Spring Data JPA의 Page 인터페이스 응답 구조 대응
export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // 현재 페이지 인덱스 (0부터 시작)
  first: boolean;
  last: boolean;
  empty: boolean;
}

// --- 상세 페이지용 추가 타입 ---

export interface ScoreResult {
  ticker: string;
  name: string;
  totalScore: number;
  grade: string;
  stabilityScore: number;
  profitabilityScore: number;
  valuationScore: number;
  investmentScore: number;
  isOpportunity: boolean;
}

export interface FinancialDetail {
  year: number;
  quarter: number;
  revenue: number;
  operatingProfit: number;
  netIncome: number;
  totalAssets: number;
  totalLiabilities: number;
  totalEquity: number;
  operatingCashFlow: number;
  researchAndDevelopment: number;
  capitalExpenditure: number;
}

// 백엔드 CompanyDetailResponse DTO 대응
export interface CompanyDetailResponse {
  info: Company;
  score: ScoreResult;
  latestFinancial: FinancialDetail;
  financialHistory: FinancialDetail[];
}
