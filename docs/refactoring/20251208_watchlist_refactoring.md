# 📄 관심 종목(Watchlist) 도메인 구조 개선 및 성능 최적화

## 개요

  * **작성일:** 2025년 12월 08일
  * **대상 모듈:** Backend - Watchlist Domain
  * **작업 목적:**
      * **아키텍처 개선:** 비즈니스 로직이 Controller에 집중된 'Fat Controller' 형태를 개선하여 책임 분리.
      * **성능 최적화:** JPA 조회 시 발생하는 **N+1 문제** 해결.
      * **유지보수성 향상:** 중복 코드 제거 및 트랜잭션 관리 강화.

-----

## 문제점 분석

### 계층 간 책임 불분명 (Fat Controller)

기존 `WatchlistController`가 요청 처리뿐만 아니라 비즈니스 로직(유효성 검사, 데이터 가공)과 데이터 액세스(Repository 호출)까지 모두 담당하고 있었습니다.

  * **문제점:** 코드가 길어지고 가독성이 떨어지며, 재사용이 불가능함. 트랜잭션 관리가 Controller 레벨에서 이루어져 확장이 어려움.

### 조회 성능 저하 (N+1 문제)

관심 종목 목록을 조회하는 과정에서 심각한 성능 비효율이 발생했습니다.

  * **현상:** `watchlistRepository.findByMember(member)`로 목록을 가져온 후, Loop를 돌며 각 종목의 `CompanyScore`를 별도로 조회함.
  * **쿼리 발생:** 목록 조회 1회 + (목록 개수 N개 \* 점수 조회 1회) = **총 1 + N회의 쿼리 발생**.

### 보안 및 유효성 검증 미흡

  * 삭제 로직(`deleteWatchlist`)에서 단순히 ID만으로 삭제를 수행하여, 타인의 관심 종목을 삭제할 수 있는 잠재적 보안 취약점이 존재했습니다.

-----

## 개선 내용

### Layered Architecture 적용 (Service 계층 도입)

Controller에 있던 모든 비즈니스 로직을 신규 생성한 `WatchlistService`로 이관했습니다.

  * **Controller:** HTTP 요청/응답 처리, 파라미터 파싱에만 집중.
  * **Service:** `@Transactional`을 통한 트랜잭션 관리, 도메인 로직 수행.
  * **Repository:** 순수 데이터 접근 담당.

### Fetch Join을 통한 성능 최적화

JPA의 `JOIN FETCH`를 사용하여 연관된 엔티티(`Company`, `CompanyScore`)를 한 번의 쿼리로 조회하도록 변경했습니다.

**[수정 전 코드]**

```java
// N번의 추가 쿼리 발생
List<WatchlistResponse> list = watchlistRepository.findByMember(member).stream()
    .map(w -> {
        CompanyScore score = companyScoreRepository.findByCompany(w.getCompany()) ...
        return new WatchlistResponse(...);
    }).toList();
```

**[수정 후 코드]**

```java
// Repository
@Query("SELECT w FROM Watchlist w JOIN FETCH w.company c LEFT JOIN FETCH c.companyScore WHERE w.member = :member")
List<Watchlist> findAllByMemberWithCompanyAndScore(@Param("member") Member member);

// Service
return watchlistRepository.findAllByMemberWithCompanyAndScore(member).stream()
        .map(this::convertToDto) // 이미 Fetch된 데이터 사용 (추가 쿼리 0)
        .toList();
```

### 3.3. 보안 로직 강화

삭제 요청 시, 해당 관심 종목이 요청한 회원의 소유인지 확인하는 검증 로직을 추가했습니다.

```java
if (!watchlistRepository.existsByIdAndMember(watchlistId, member)) {
    throw new BusinessException(ErrorCode.HANDLE_ACCESS_DENIED);
}
```

-----

## 개선 결과 (Result)

  * **가독성:** Controller의 코드 라인 수가 약 60% 감소하여 핵심 로직 파악이 쉬워짐.
  * **안전성:** 엔티티 간의 객체 그래프 탐색 시 Lazy Loading 예외 발생 가능성을 차단하고, 데이터 누락(Null) 처리를 중앙화함.


