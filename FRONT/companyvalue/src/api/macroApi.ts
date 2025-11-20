import { MacroData } from "../types/macro";
import axiosClient from "./axiosClient";

export const macroApi = {
  
  // 최신 지표 1건 (요약 카드용)
  getLatest: async (): Promise<MacroData> => {
    const response = await axiosClient.get<MacroData>('/api/macro/latest');
    return response.data;
  },

  // 최근 30일 데이터 (차트용)
  getHistory: async (): Promise<MacroData[]> => {
    const response = await axiosClient.get<MacroData[]>('/api/macro/history');
    return response.data;
  }

};