import { PolarAngleAxis, PolarGrid, PolarRadiusAxis, Radar, RadarChart, ResponsiveContainer, Tooltip } from "recharts";
import { ScoreResult } from "../../types/company";

interface Props {
  score: ScoreResult;
}

const ScoreRadarChart = ({ score }: Props) => {
  // 차트용 데이터 변환
  const data = [
    { subject: "안정성", A: score.stabilityScore, fullMark: 40 }, // 40점 만점
    { subject: "수익성", A: score.profitabilityScore, fullMark: 30 }, // 30점 만점
    { subject: "가치", A: score.valuationScore, fullMark: 20 }, // 20점 만점
    { subject: "미래투자", A: score.investmentScore, fullMark: 10 },
  ];

  // 각 항목별 만점 대비 비율로 환산 (시각적 균형을 위해 100점 만점으로 정규화 가능하나, 여기선 원본 점수 표기)
  
  return (
    <div className="w-full h-[300px]">
      <ResponsiveContainer width="100%" height="100%">
        <RadarChart cx="50%" cy="50%" outerRadius="80%" data={data}>
          <PolarGrid stroke="#475569" />
          <PolarAngleAxis dataKey="subject" tick={{ fill: "#94a3b8", fontSize: 12 }} />
          <PolarRadiusAxis angle={30} domain={[0, 'auto']} tick={false} axisLine={false} />
          <Radar
            name="Score"
            dataKey="A"
            stroke="#60a5fa"
            strokeWidth={3}
            fill="#3b82f6"
            fillOpacity={0.4}
          />
          <Tooltip 
            contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#f1f5f9' }}
            itemStyle={{ color: '#60a5fa' }}
          />
        </RadarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default ScoreRadarChart;
