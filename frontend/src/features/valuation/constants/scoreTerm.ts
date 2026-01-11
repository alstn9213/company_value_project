import { TermDefinition } from "../../../types/term";

// 분석 리포트용 용어 정의
export const SCORE_TERMS: Record<string, TermDefinition> = {
  stability: {
    title: "안정성 (Stability)",
    description:
      "회사가 망하지 않고 빚을 잘 갚을 수 있는지 확인합니다.\n\n• 부채비율: 회사 돈보다 빚이 얼마나 많은가를 나타냅니다. \n• 유동비율: 당장 빚을 갚을 돈이 얼마나 있는가 나타냅니다.",
  },
  profitability: {
    title: "수익성 (Profitability)",
    description:
      "회사가 장사를 얼마나 효율적으로 하는지 확인합니다.\n\n• ROE (자기자본이익률): 주주의 돈을 굴려서 이익을 얼마나 냈는가 나타냅니다.\n• 영업이익률: 물건 하나 팔 때마다 얼마가 남는지 나타냅니다.",
  },
  valuation: {
    title: "내재가치 (Valuation)",
    description:
      "현재 주가가 싼지 비싼지 판단합니다.\n\n• PER (주가수익비율): 회사가 버는 돈에 비해 주가가 몇 배인가 나타냅니다.\n• PBR (주가순자산비율): 회사가 가진 재산에 비해 주가가 몇 배인가 나타냅니다.",
  },
  investment: {
    title: "미래투자 (Future Investment)",
    description:
      "회사가 미래 성장을 위해 돈을 쓰고 있는지 확인합니다.\n\n• R&D 비율: 매출의 일정 부분을 연구개발에 재투자하는지 봅니다. 당장은 비용이지만 미래의 성장 엔진이 됩니다.",
  },
};