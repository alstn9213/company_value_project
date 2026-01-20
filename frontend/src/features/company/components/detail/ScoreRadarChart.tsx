import {
  PolarAngleAxis,
  PolarGrid,
  PolarRadiusAxis,
  Radar,
  RadarChart,
  ResponsiveContainer,
  Tooltip,
} from "recharts";
import { ChartDataPoint } from "../../types/chartDataPoint";
import { CustomTooltip } from "../../ui/p_detail/CustomTooltip";
import { useScoreRadarData } from "../../hooks/useScoreRadarData";
import { EmptyState } from "../../../../components/ui/EmptyState";
import { PieChart } from "lucide-react";


interface ScoreRadarChartProps {
  data: ChartDataPoint[];
}

export const ScoreRadarChart: React.FC<ScoreRadarChartProps> = ({ data }) => {

  const { normalizedData, hasData } = useScoreRadarData(data);

  if (!hasData) {
    return (
      <div style={{ width: '100%', height: 300 }} className="flex items-center justify-center">
        <EmptyState 
          icon={<PieChart size={32} className="text-slate-600" />}
          title="차트 데이터 없음"
          description="표시할 점수 데이터가 부족합니다."
          className="p-0" // 내부 패딩 최소화
        />
      </div>
    );
  }

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

