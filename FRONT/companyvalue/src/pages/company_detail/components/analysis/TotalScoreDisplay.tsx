import { getScoreColor } from "../../../../utils/formatters";

interface Props {
  totalScore: number;
}

const TotalScoreDisplay = ({ totalScore }: Props) => {
  return (
    <div className="text-center mb-8 relative">
      <span className="text-slate-400 text-sm uppercase tracking-wider">
        Total Score
      </span>
      
      <div className={`text-6xl font-black mt-2 tracking-tight ${getScoreColor(totalScore)}`}>
        {totalScore}
        <span className="text-2xl text-slate-600 font-medium ml-1">
          /100
        </span>
      </div>
    </div>
  );
};

export default TotalScoreDisplay;