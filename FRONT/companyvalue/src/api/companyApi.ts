import { Company, CompanyDetailResponse, PageResponse, StockHistory } from "../types/company";
import axiosClient from "./axiosClient";

export const companyApi = {
  
  // 전체 목록 조회 (페이징)
  getAll: async (page: number, size: number = 12, sortOption: string = 'name'): Promise<PageResponse<Company>> => {
    const response = await axiosClient.get<PageResponse<Company>>('/api/companies', {
    params: { page, size, sort: sortOption }
    });
    return response.data;
  },

  // 기업 검색(이름)
  search: async (keyword: string): Promise<Company[]> => {
    const response = await axiosClient.get<Company[]>('/api/companies/search', {
      params: {keyword}
    });
    return response.data;
  },

  // 기업 상세 조회 (정보 + 점수 + 재무)
  getDetail: async (ticker: string): Promise<CompanyDetailResponse> => {
    const response = await axiosClient.get<CompanyDetailResponse>(`/api/companies/${ticker}`);
    return response.data;
  },

  // 주가 차트 데이터 조회
  getStockHistory: async (ticker: string): Promise<StockHistory[]> => {
    const response = await axiosClient.get<StockHistory[]>(`/api/companies/${ticker}/chart`);
    return response.data;
  }

};