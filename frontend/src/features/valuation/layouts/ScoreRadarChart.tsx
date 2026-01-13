import {
  PolarAngleAxis,
  PolarGrid,
  PolarRadiusAxis,
  Radar,
  RadarChart,
  ResponsiveContainer,
  Tooltip,
} from "recharts";
import { ChartDataPoint } from "../types/chartDataPoint";
import { CustomTooltip } from "../components/CustomTooltip";


interface ScoreRadarChartProps {
  data: ChartDataPoint[];
}

export const ScoreRadarChart: React.FC<ScoreRadarChartProps> = ({ data }) => {

  // 1. 데이터 변환: 모든 점수를 100점 만점 기준으로 환산
  const normalizedData = data.map((item) => ({
    ...item,
    // 그래프용 환산 점수: (내점수 / 만점) * 100
    normalizedScore: (item.score / (item.fullMark || 100)) * 100,
  }));
  // 각 항목별 만점 대비 비율로 환산 (시각적 균형을 위해 100점 만점으로 정규화 가능하나, 여기선 원본 점수 표기)

  return (
    <div style={{ width: '100%', height: 300 }}>
      <ResponsiveContainer 
        width="100%" 
        height="100%" 
        minWidth={0} 
        minHeight={0}
      >
        <RadarChart cx="50%" cy="50%" outerRadius="70%" data={normalizedData}>
          <PolarGrid stroke="#334155" />
          <PolarAngleAxis 
            dataKey="subject" 
            tick={{ fill: '#94a3b8', fontSize: 12 }} 
          />
          {/* 도메인을 [0, 100]으로 고정하여 그래프가 꽉 차게 보이도록 설정 */}
          <PolarRadiusAxis 
            angle={30} 
            domain={[0, 100]} 
            tick={false} 
            axisLine={false} 
          />
          
          <Radar
            name="Score"
            dataKey="normalizedScore" // 그래프는 환산 점수를 사용
            stroke="#10b981"
            fill="#10b981"
            fillOpacity={0.3}
          />
          
          <Tooltip content={<CustomTooltip />} />

        
        </RadarChart>
      </ResponsiveContainer>
    </div>
  );
};

