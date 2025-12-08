package com.back.domain.watchlist.repository;

import com.back.domain.member.entity.Member;
import com.back.domain.watchlist.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    // N+1 문제 해결: Company와 CompanyScore를 함께 Fetch Join
    @Query("SELECT w FROM Watchlist w " +
            "JOIN FETCH w.company c " +
            "LEFT JOIN FETCH c.companyScore " +
            "WHERE w.member = :member")
    List<Watchlist> findAllByMemberWithCompanyAndScore(@Param("member") Member member);

    // 중복 추가 방지용
    boolean existsByMemberAndCompanyTicker(Member member, String ticker);

    // 삭제 시 본인 소유인지 확인하기 위해 필요할 수 있음
    boolean existsByIdAndMember(Long id, Member member);
}
