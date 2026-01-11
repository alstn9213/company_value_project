import { Company, CompanyDetailResponse, PageResponse, ScoreResult, StockHistory } from "../types/company";
import axiosClient from "./axiosClient";

export const companyApi = {
  
  getAll: async (page: number, size: number = 12, sortOption: string = 'name'): Promise<PageResponse<Company>> => {
    const response = await axiosClient.get<PageResponse<Company>>('/api/companies', {
      params: { page, size, sort: sortOption }
    });
    return response.data;
  },

  search: async (keyword: string): Promise<Company[]> => {
    const response = await axiosClient.get<Company[]>('/api/companies/search', {
      params: { keyword }
    });
    return response.data;
  },

  getDetail: async (ticker: string): Promise<CompanyDetailResponse> => {
    const response = await axiosClient.get<CompanyDetailResponse>(`/api/companies/${ticker}`);
    return response.data;
  },

  getStockHistory: async (ticker: string): Promise<StockHistory[]> => {
    const response = await axiosClient.get<StockHistory[]>(`/api/companies/${ticker}/chart`);
    return response.data;
  },

  getTopRanked: async (): Promise<ScoreResult[]> => {
    const res = await axiosClient.get<ScoreResult[]>("/api/scores/top");
    return res.data;
  },

};