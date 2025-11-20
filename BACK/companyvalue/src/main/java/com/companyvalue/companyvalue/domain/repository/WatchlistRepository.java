package com.companyvalue.companyvalue.domain.repository;

import com.companyvalue.companyvalue.domain.Member;
import com.companyvalue.companyvalue.domain.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    // 특정 회원의 관심 목록 조회 (Join Fetch로 N+1 문제 방지 권장)
    List<Watchlist> findByMember(Member member);

    // 중복 추가 방지용
    boolean existsByMemberAndCompanyTicker(Member member, String ticker);

    Optional<Watchlist> findByMemberAndCompanyTicker(Member member, String ticker);
}
