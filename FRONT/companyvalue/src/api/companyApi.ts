import { DUMMY_COMPANIES } from "../mocks/dummyCompanies";
import { Company, CompanyDetailResponse, PageResponse, StockHistory } from "../types/company";
import axiosClient from "./axiosClient";

export const companyApi = {
  
  // 전체 목록 조회 (페이징)
  getAll: async (page: number, size: number = 12, sortOption: string = 'name'): Promise<PageResponse<Company>> => {
    try {
      const response = await axiosClient.get<PageResponse<Company>>('/api/companies', {
      params: { page, size, sort: sortOption }
      });
      if (page === 0) {
          // 더미 데이터 객체를 리스트 배열로 변환
          const dummyList: Company[] = Object.values(DUMMY_COMPANIES).map(d => d.info);
          
          return {
            ...response.data,
            content: [...dummyList, ...response.data.content] // [더미들, ...실제데이터] 순서로 합침
          };
        }

        return response.data;

    } catch (error) {
      // (옵션) 서버가 꺼져있을 때 더미 데이터라도 보여주려면 에러 처리에서 리턴
      console.error("서버 연결 실패, 더미 데이터만 반환합니다.", error);
      if (page === 0) {
        const dummyList: Company[] = Object.values(DUMMY_COMPANIES).map(d => d.info);
        return {
          content: dummyList,
          totalPages: 1,
          totalElements: dummyList.length,
          size: size,
          number: 0,
          first: true,
          last: true,
          empty: false
        };
      }
      throw error;
    }
  },

  // 기업 검색(이름)
  search: async (keyword: string): Promise<Company[]> => {
    const response = await axiosClient.get<Company[]>('/api/companies/search', {
      params: {keyword}
    });
    const lowerKeyword = keyword.toLowerCase();
    const matchedDummies = Object.values(DUMMY_COMPANIES)
      .map(d => d.info)
      .filter(c =>
        c.name.toLowerCase().includes(lowerKeyword) ||
        c.ticker.toLowerCase().includes(lowerKeyword)
      );

    // (3) 합쳐서 반환
    return [...matchedDummies, ...response.data];
  },

  // 기업 상세 조회 (정보 + 점수 + 재무)
  getDetail: async (ticker: string): Promise<CompanyDetailResponse> => {
    // 요청된 Ticker가 Mock 데이터에 있는지 확인
    const upperTicker = ticker.toUpperCase();
    if(DUMMY_COMPANIES[upperTicker]) {
      console.log(`[Mock API] Returning dummy data for ${upperTicker}`);

      return new Promise((resolve) => {
        setTimeout(() => {
          resolve(DUMMY_COMPANIES[upperTicker]);
        }, 500);
      });
    }

    const response = await axiosClient.get<CompanyDetailResponse>(`/api/companies/${ticker}`);

    return response.data;
  },

  // 주가 차트 데이터 조회
  getStockHistory: async (ticker: string): Promise<StockHistory[]> => {
    if (DUMMY_COMPANIES[ticker.toUpperCase()]) return [];
    const response = await axiosClient.get<StockHistory[]>(`/api/companies/${ticker}/chart`);
    return response.data;
  }

};