import { CompanyScoreResponse } from "../../../../types/company";
import { MAX_SCORES } from "../../constants/maxScores";
import { SCORE_TERMS } from "../../constants/scoreTerm";
import { ScoreRow } from "../../ui/p_detail/ScoreRow";

type DetailScoreProps = Pick<
  CompanyScoreResponse, 
  'stabilityScore' | 'profitabilityScore' | 'valuationScore' | 'investmentScore'
>;

interface Props {
  score: DetailScoreProps;
}

export const DetailScore = ({ score }: Props) => {
  const scoreItems = [
    {
      label: "안정성 (부채/유동성)",
      value: score.stabilityScore,
      max: MAX_SCORES.STABILITY,
      color: "bg-blue-500",
      term: SCORE_TERMS.stability,
    },
    {
      label: "수익성 (ROE/마진)",
      value: score.profitabilityScore,
      max: MAX_SCORES.PROFITABILITY,
      color: "bg-emerald-500",
      term: SCORE_TERMS.profitability,
    },
    {
      label: "내재가치 (PER/PBR)",
      value: score.valuationScore,
      max: MAX_SCORES.VALUATION,
      color: "bg-purple-500",
      term: SCORE_TERMS.valuation,
    },
    {
      label: "미래투자 (R&D)",
      value: score.investmentScore,
      max: MAX_SCORES.INVESTMENT,
      color: "bg-orange-500",
      term: SCORE_TERMS.investment,
    },
  ];

  return (
    <div className="mt-8 space-y-4 pt-6 border-t border-slate-800">
      {scoreItems.map((item) => (
        <ScoreRow
          key={item.label}
          label={item.label}
          value={item.value}
          max={item.max}
          color={item.color}
          term={item.term}
        />
      ))}
    </div>
  );
};
