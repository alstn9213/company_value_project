import { CompanyDetailResponse } from "../types/company";

// 더미 데이터 모음
export const DUMMY_COMPANIES: Record<string, CompanyDetailResponse> = {
  // 1. 자본 잠식 기업 (F등급)
  "ZOMB": {
    info: {
      ticker: "ZOMB",
      name: "Zombie Tech Inc.",
      sector: "Technology",
      exchange: "NASDAQ",
      totalScore: 0,
      grade: "F",
    },
    score: {
      ticker: "ZOMB",
      name: "Zombie Tech Inc.",
      totalScore: 0,
      grade: "F",
      stabilityScore: 0,
      profitabilityScore: 0,
      valuationScore: 0,
      investmentScore: 0,
      isOpportunity: false,
    },
    latestFinancial: {
      year: 2024,
      quarter: 1,
      revenue: 10000000,
      operatingProfit: -5000000,
      netIncome: -6000000,
      totalAssets: 50000000,
      totalLiabilities: 60000000,
      totalEquity: -10000000, // 자본 잠식
      operatingCashFlow: -2000000,
      researchAndDevelopment: 1000000,
      capitalExpenditure: 500000,
    },
    financialHistory: [] // 차트용 과거 데이터 (생략 가능)
  },

  // 2. 부채 과다 기업 (F등급)
  "DEBT": {
    info: {
      ticker: "DEBT",
      name: "Heavy Debt Corp.",
      sector: "Industrial",
      exchange: "NYSE",
      totalScore: 0,
      grade: "F",
    },
    score: {
      ticker: "DEBT",
      name: "Heavy Debt Corp.",
      totalScore: 0,
      grade: "F",
      stabilityScore: 10,
      profitabilityScore: 15,
      valuationScore: 5,
      investmentScore: 5,
      isOpportunity: false,
    },
    latestFinancial: {
      year: 2024,
      quarter: 1,
      revenue: 80000000,
      operatingProfit: 5000000,
      netIncome: 3000000,
      totalAssets: 60000000,
      totalLiabilities: 50000000,
      totalEquity: 10000000, // 부채비율 500%
      operatingCashFlow: 4000000,
      researchAndDevelopment: 0,
      capitalExpenditure: 2000000,
    },
    financialHistory: []
  },

  // 3. 우량 기업 (S등급)
  "BEST": {
    info: {
      ticker: "BEST",
      name: "Diamond Holdings",
      sector: "Financial Services",
      exchange: "NYSE",
      totalScore: 94,
      grade: "S",
    },
    score: {
      ticker: "BEST",
      name: "Diamond Holdings",
      totalScore: 94,
      grade: "S",
      stabilityScore: 38,
      profitabilityScore: 28,
      valuationScore: 18,
      investmentScore: 10,
      isOpportunity: true,
    },
    latestFinancial: {
      year: 2024,
      quarter: 1,
      revenue: 100000000,
      operatingProfit: 25000000,
      netIncome: 21000000,
      totalAssets: 150000000,
      totalLiabilities: 50000000,
      totalEquity: 100000000,
      operatingCashFlow: 30000000,
      researchAndDevelopment: 15000000,
      capitalExpenditure: 10000000,
    },
    financialHistory: []
  },

  // 4. 고평가 성장주 (C등급)
  "GROW": {
    info: {
      ticker: "GROW",
      name: "Hyper Growth Inc.",
      sector: "Technology",
      exchange: "NASDAQ",
      totalScore: 65,
      grade: "C",
    },
    score: {
      ticker: "GROW",
      name: "Hyper Growth Inc.",
      totalScore: 65,
      grade: "C",
      stabilityScore: 35,
      profitabilityScore: 30, // 수익성은 만점
      valuationScore: 0,      // 너무 비싸서 0점
      investmentScore: 0,
      isOpportunity: false,
    },
    latestFinancial: {
      year: 2024,
      quarter: 1,
      revenue: 50000000,
      operatingProfit: 15000000,
      netIncome: 12000000,
      totalAssets: 40000000,
      totalLiabilities: 10000000,
      totalEquity: 30000000,
      operatingCashFlow: 10000000,
      researchAndDevelopment: 10000000,
      capitalExpenditure: 5000000,
    },
    financialHistory: []
  }
};