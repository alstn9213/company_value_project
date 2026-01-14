export interface CompanySummaryResponse {
  ticker: string;
  name: string;
  sector: string;
  exchange: string;
  totalScore: number;
  grade: string;
}

// Spring Data JPA의 Page 인터페이스 응답 구조 대응
export interface PageResponse<T> {
  content: T[];
  page: {
    size: number;
    number: number; // 현재 페이지
    totalElements: number;
    totalPages: number;
  };
}

export interface CompanyScoreResponse {
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


export interface FinancialStatementResponse {
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

export interface CompanyDetailResponse {
  companySummary: CompanySummaryResponse;
  score: CompanyScoreResponse;
  latestFinancial: FinancialStatementResponse;
  financialHistory: FinancialStatementResponse[];
}

export interface StockHistoryResponse {
  date: string; // "2024-01-01"
  close: number; // 150.50
}