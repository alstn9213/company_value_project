import axiosClient from "../../../api/axiosClient";
import { MacroDataResponse } from "../../../types/macro";

export const macroApi = {
  
  // 최신 지표 1건 (요약 카드용)
  getLatest: async (): Promise<MacroDataResponse> => {
    const response = await axiosClient.get<MacroDataResponse>('/api/macro/latest');
    return response.data;
  },

  // 최근 10년 데이터 (차트용)
  getHistory: async (): Promise<MacroDataResponse[]> => {
    const response = await axiosClient.get<MacroDataResponse[]>('/api/macro/history');
    return response.data;
  }

};