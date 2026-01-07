package com.back.domain.watchlist.service;

import com.back.domain.company.entity.Company;
import com.back.domain.company.entity.CompanyScore;
import com.back.domain.company.repository.CompanyRepository;
import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.domain.watchlist.dto.WatchlistResponse;
import com.back.domain.watchlist.entity.Watchlist;
import com.back.domain.watchlist.repository.WatchlistRepository;
import com.back.global.error.ErrorCode;
import com.back.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchlistService {

  private final WatchlistRepository watchlistRepository;
  private final MemberRepository memberRepository;
  private final CompanyRepository companyRepository;

  public List<WatchlistResponse> getWatchlist(Long memberId) {
    Member member = getMember(memberId);

    return watchlistRepository.findAllByMemberWithCompanyAndScore(member).stream()
            .map(WatchlistResponse::from)
            .toList();
  }

  @Transactional // 쓰기 메서드는 따로 Transactional을 붙인다.
  public void addWatchlist(Long memberId, String ticker) {
    Member member = getMember(memberId);
    Company company = companyRepository.findByTicker(ticker)
            .orElseThrow(() -> new BusinessException(ErrorCode.COMPANY_NOT_FOUND));

    if (watchlistRepository.existsByMemberAndCompanyTicker(member, ticker)) {
      throw new BusinessException(ErrorCode.WATCHLIST_DUPLICATION);
    }

    watchlistRepository.save(Watchlist.builder()
            .member(member)
            .company(company)
            .build());
  }

  @Transactional // 쓰기 메서드는 따로 Transactional을 붙인다.
  public void deleteWatchlist(Long memberId, Long watchlistId) {
    Member member = getMember(memberId);

    // 내 관심종목인지 확인 후 삭제 (보안 강화)
    if(!watchlistRepository.existsByIdAndMember(watchlistId, member)) {
      throw new BusinessException(ErrorCode.WATCHLIST_ACCESS_DENIED);
    }

    watchlistRepository.deleteById(watchlistId);
  }

  // --- 헬퍼 메서드 ---

  // 회원이 있는지 확인하는 헬퍼 메서드
  private Member getMember(Long memberId) {
    return memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
  }
}
