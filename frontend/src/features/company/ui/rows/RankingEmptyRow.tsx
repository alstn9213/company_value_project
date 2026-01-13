import { ClipboardList } from "lucide-react";
import { EmptyState } from "../../../../components/ui/EmptyState";

/**
 * 데이터가 없을 때 보여줄 컴포넌트
 */
export const RankingEmptyRow = () => (
  <tr>
    <td colSpan={4} className="py-10">
      <EmptyState
        icon={<ClipboardList size={40} />}
        title="랭킹 데이터가 없습니다."
        description="집계된 우량주 데이터가 아직 없습니다."
      />
    </td>
  </tr>
);