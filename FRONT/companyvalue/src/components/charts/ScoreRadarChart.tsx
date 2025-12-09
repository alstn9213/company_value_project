import {
  PolarAngleAxis,
  PolarGrid,
  PolarRadiusAxis,
  Radar,
  RadarChart,
  ResponsiveContainer,
  Tooltip,
} from "recharts";

export interface ChartDataPoint {
  subject: string;
  score: number;
  fullMark: number;
  normalizedScore?: number; // 내부 계산용
}


interface ScoreRadarChartProps {
  data: ChartDataPoint[];
}

// Recharts의 타입 오류를 피하기 위해 직접 인터페이스 정의
interface CustomTooltipProps {
  active?: boolean;
  payload?: {
    value: number;       // 차트 값 (여기선 환산 점수)
    payload: ChartDataPoint; // 원본 데이터 객체
  }[];
  label?: string;
}

// 커스텀 툴팁 컴포넌트
const CustomTooltip = ({active, payload}: CustomTooltipProps) => {
  if (active && payload && payload.length) {
    // payload[0].payload는 any 타입일 수 있으므로 ChartDataPoint로 단언
    const data = payload[0].payload as ChartDataPoint;
    
    return (
      <div className="bg-slate-900/95 border border-slate-700 p-3 rounded-lg shadow-xl backdrop-blur-md">
        <p className="font-bold text-slate-100 mb-1">{data.subject}</p>
        <div className="text-sm">
          <span className="text-slate-400">점수: </span>
          <span className="text-emerald-400 font-bold ml-1">
            {data.score}
          </span>
          <span className="text-slate-600 mx-1">/</span>
          <span className="text-slate-500">{data.fullMark}</span>
        </div>
        <div className="text-xs text-slate-600 mt-1">
          (100점 환산: {Math.round((data.score / data.fullMark) * 100)}점)
        </div>
      </div>
    );
  }
  return null;
};

const ScoreRadarChart: React.FC<ScoreRadarChartProps> = ({ data }) => {

  // 1. 데이터 변환: 모든 점수를 100점 만점 기준으로 환산
  const normalizedData = data.map((item) => ({
    ...item,
    // 그래프용 환산 점수: (내점수 / 만점) * 100
    normalizedScore: (item.score / (item.fullMark || 100)) * 100,
  }));
  // 각 항목별 만점 대비 비율로 환산 (시각적 균형을 위해 100점 만점으로 정규화 가능하나, 여기선 원본 점수 표기)

  return (
    <div style={{ width: '100%', height: 300 }}>
      <ResponsiveContainer width="100%" height="100%">
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

export default ScoreRadarChart;
