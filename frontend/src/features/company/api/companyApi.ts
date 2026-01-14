import { CompanySummaryResponse, CompanyDetailResponse, PageResponse, CompanyScoreResponse, StockHistoryResponse } from "../../../types/company";
import axiosClient from "../../../api/axiosClient";

export const companyApi = {
  
  getAll: async (page: number, size: number = 12, sortOption: string = 'name'): Promise<PageResponse<CompanySummaryResponse>> => {
    const response = await axiosClient.get<PageResponse<CompanySummaryResponse>>('/api/companies', {
      params: { page, size, sort: sortOption }
    });
    return response.data;
  },

  search: async (keyword: string): Promise<CompanySummaryResponse[]> => {
    const response = await axiosClient.get<CompanySummaryResponse[]>('/api/companies/search', {
      params: { keyword }
    });
    return response.data;
  },

  getDetail: async (ticker: string): Promise<CompanyDetailResponse> => {
    const response = await axiosClient.get<CompanyDetailResponse>(`/api/companies/${ticker}`);
    return response.data;
  },

  getStockHistoryResponse: async (ticker: string): Promise<StockHistoryResponse[]> => {
    const response = await axiosClient.get<StockHistoryResponse[]>(`/api/companies/${ticker}/chart`);
    return response.data;
  },

  getTopRanked: async (): Promise<CompanyScoreResponse[]> => {
    const res = await axiosClient.get<CompanyScoreResponse[]>("/api/scores/top");
    return res.data;
  },

};