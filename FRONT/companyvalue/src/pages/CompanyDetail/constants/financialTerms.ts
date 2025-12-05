export interface TermDefinition {
  title: string;
  description: string;
}

// Record<string, TermDefinition> 타입을 사용하여
// 키(key)는 문자열, 값(value)은 TermDefinition 객체임을 명시합니다.
export const FINANCIAL_TERMS: Record<string, TermDefinition> = {
  revenue: {
    title: "매출액 (Revenue)",
    description:
      "회사가 물건이나 서비스를 팔아서 번 '전체 수입'입니다.\n비용을 빼기 전, 순수하게 회사의 덩치가 얼마나 큰지를 보여주는 가장 기초적인 지표입니다.",
  },
  operatingProfit: {
    title: "영업이익 (Operating Profit)",
    description:
      "매출액에서 재료비, 인건비, 임대료 등 장사하는 데 들어가는 필수 비용을 뺀 돈입니다.\n회사가 '본업'을 얼마나 잘해서 돈을 벌었는지 보여주는 가장 중요한 성적표입니다.",
  },
  netIncome: {
    title: "당기순이익 (Net Income)",
    description:
      "영업이익에서 세금, 은행 이자 등 나갈 돈을 모두 다 내고 최종적으로 남은 돈입니다.\n실제로 회사 주머니에 들어오는 '진짜 순이익'을 의미합니다.",
  },
  operatingCashFlow: {
    title: "영업활동 현금흐름 (Operating Cash Flow)",
    description:
      "장부상의 이익이 아니라, 실제로 회사의 통장에 '현금이 얼마나 들어왔는지'를 보여줍니다.\n이 돈이 플러스(+)여야 월급도 주고 빚도 갚으며 회사가 안정적으로 굴러갑니다.",
  },
  totalAssets: {
    title: "자산 총계 (Total Assets)",
    description:
      "회사가 가지고 있는 '모든 재산'입니다.\n현금, 건물, 기계뿐만 아니라 나중에 받을 돈(채권)까지 모두 포함됩니다. (자산 = 자본 + 부채)",
  },
  totalLiabilities: {
    title: "부채 총계 (Total Liabilities)",
    description:
      "회사가 남에게 갚아야 할 '빚'입니다.\n은행 대출이나 외상값 등이 여기에 속합니다. 부채가 너무 많으면 회사가 위험해질 수 있습니다.",
  },
  totalEquity: {
    title: "자본 총계 (Total Equity)",
    description:
      "전체 재산(자산)에서 빚(부채)을 다 갚고 남은 '순수한 회사의 돈'입니다.\n주주들의 몫이기도 하며, 이 금액이 꾸준히 늘어나야 좋은 회사입니다.",
  },
  researchAndDevelopment: {
    title: "R&D 투자비용 (Research & Development)",
    description:
      "회사가 더 좋은 제품을 만들기 위해 '연구 개발에 쓴 돈'입니다.\n당장은 비용이 나가지만, 미래의 성장을 위해 꼭 필요한 투자 비용입니다.",
  },
  capitalExpenditure: {
    title: "설비 투자 (CapEx)",
    description:
      "공장, 기계, 건물 같은 '설비에 투자한 돈'입니다.\n'우리 회사가 앞으로 더 많이 생산하고 성장하겠습니다'라는 의지를 보여주는 지출입니다.",
  },
};