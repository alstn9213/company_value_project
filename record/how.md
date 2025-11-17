# 프로젝트 개요

- 기업들의 재무 정보들을 활용해서 주식 투자하기에 적절한 기업인지 판단하는데 도움을 주는 웹사이트 제작.

- 총점은 100점으로 해서 재무상태를 바탕으로 기업마다 점수를 매긴다.

- 웹사이트의 목적은 어디까지나 투자하기에 재무상태가 위험한 기업을 피하고 저평가된 기업을 판단하는 데 있기때문에, 기업에 대한 평가 기준을 보수적으로 잡는다.

## ERD

- Member (회원): 사용자 정보를 저장합니다.
member_id (PK, 기본 키)
email (UNIQUE, 로그인 ID로 사용)
password (암호화하여 저장)
nickname
created_at (가입일)
---------------------------

- Company (기업): 분석 대상 기업의 기본 정보를 저장합니다.

company_id (PK)
ticker_symbol (종목 코드, 예: "005930") (UNIQUE)
company_name (기업명, 예: "삼성전자")
sector (업종)
FinancialStatement (재무제표): API를 통해 가져온 기업의 핵심 재무 데이터를 저장합니다.
statement_id (PK)
company_id (FK, Company 참조)
year (연도)
quarter (분기)
revenue (매출액)
operating_profit (영업이익)
net_income (당기순이익)
total_assets (자산총계)
total_liabilities (부채총계)
total_equity (자본총계)
operating_cash_flow (영업활동 현금흐름)

(...기타 필요한 재무 데이터 추가)
---------------------------

- CompanyScore (기업 점수): 계산된 기업의 점수를 저장합니다.

score_id (PK)
company_id (FK, Company 참조)
score_date (점수 계산일, 데이터 최신성 확보)
total_score (총점 100점)
stability_score (안정성 점수)
profitability_score (수익성 점수)
valuation_score (가치 점수)
---------------------------

- Watchlist (관심 목록): 회원이 관심 등록한 기업을 관리합니다. (Member와 Company 간의 N:M 관계)

watchlist_id (PK)
member_id (FK, Member 참조)
company_id (FK, Company 참조)

## 백엔드(Spring Boot)

1. API 설계 (RESTful API)

  - 회원 관리: POST /api/auth/register (회원가입), POST /api/auth/login (로그인, JWT 토큰 발급)

  - 기업 정보: GET /api/company/search?name={query} (기업 검색), GET /api/company/{ticker} (특정 기업 상세 정보)

  - 점수/재무: GET /api/score/{ticker} (기업 점수 및 재무 데이터 조회)

  - 관심 목록: POST /api/watchlist/{ticker} (관심 목록 추가), DELETE /api/watchlist/{ticker} (삭제), GET /api/watchlist (내 관심 목록 조회)

2. 핵심 비즈니스 로직 (Scoring Service)

  - ScoringService 클래스를 만들어 기업 평가 로직을 캡슐화합니다.

  - 이 서비스는 FinancialStatement 데이터를 조회하여, 3번 항목에서 제안할 '평가 기준'에 따라 100점 만점의 점수를 계산합니다.

3. 데이터베이스 연동

  - Spring Data JPA를 사용하여 ERD에 맞게 Entity와 Repository를 구성합니다.

4. 보안

  - Spring Security와 **JWT(JSON Web Token)**를 도입하여 로그인 및 API 인증을 처리합니다. 회원가입 시 비밀번호는 BCrypt 등으로 반드시 암호화합니다.

5. 외부 API 연동 및 스케줄링

  - 증권사나 공공데이터포털의 재무 정보 API를 호출해야 합니다. (WebClient사용)

  - **Spring Scheduler (@Scheduled)**를 사용해 매일 밤 또는 분기마다 기업들의 재무 데이터와 점수를 미리 계산하여 CompanyScore 테이블에 저장해두는 것이 좋습니다. (사용자가 조회할 때마다 계산하면 매우 느릴 수 있습니다.)

## 프론트엔드(React)

- 페이지 구성 (React Router)

1) MainPage: 기업을 검색하고, 랭킹(Top 10) 등을 보여주는 메인 화면.

2) CompanyDetailPage: `가장 중요한 페이지`
  - 기업의 총점(게이지, 차트 등으로 시각화)
  - 세부 평가 항목(안정성/수익성/가치)
  - 핵심 재무제표 차트(매출, 이익 추이)를 보여줍니다.

3) LoginPage: 회원가입 및 로그인 폼.

4) MyPage / WatchlistPage: 내 정보 수정 및 관심 목록 관리.

- 상태 관리 (State Management)

로그인한 유저 정보(토큰, 닉네임)는 전역 상태로 관리해야 합니다. (Redux Toolkit이나 Zustand 사용을 권장합니다.)

기업 검색 결과, 상세 정보 등 서버 데이터는 **React Query (TanStack Query)**를 사용하면 캐싱, 로딩, 에러 처리가 매우 편리합니다.

- API 통신

Axios 라이브러리를 설치하여 Spring Boot API와 통신합니다. 로그인 시 발급받은 JWT 토큰을 Authorization 헤더에 담아 요청을 보냅니다.

- 데이터 시각화

기업의 재무 추이(매출, 영업이익)나 점수 분포를 보여주기 위해 Recharts 또는 Chart.js 같은 차트 라이브러리를 활용합니다.

## 기업 평가 아이디어 (보수적 관점)

`목표가 '위험한 기업을 피하고 저평가된 기업을 찾는 것'이므로, '안정성'에 가장 큰 가중치를 두어야 합니다. (총점 100점)`

1. 안정성 (가중치: 40점)
   `"이 기업이 망하지 않을 것인가?"에 대한 평가입니다.`

부채비율 (Debt-to-Equity Ratio): (총부채 / 총자본) \* 100

보수적 기준: 100% 미만 (만점), 200% 초과 시 큰 감점. (제조업 기준, 업종별 상이)

유동비율 (Current Ratio): (유동자산 / 유동부채) \* 100

보수적 기준: 200% 이상 (만점). 100% 미만 시 단기 상환 능력 문제로 큰 감점.

영업현금흐름 (Operating Cash Flow):

보수적 기준: **최근 3년 연속 플러스(+)**인가? (흑자 도산 방지)

2. 수익성 (가중치: 30점)
   `"이 기업이 돈을 잘 벌고 있는가?"에 대한 평가입니다.`

ROE (Return on Equity, 자기자본이익률): (당기순이익 / 총자본) \* 100

보수적 기준: 최근 3년 평균 10% 이상 (최소한 시중 금리보다 높아야 함).

영업이익률 (Operating Margin): (영업이익 / 매출액) \* 100

보수적 기준: 최근 3년 평균 5% 이상이며, 일관성이 있는가? (경쟁력, 마진)

매출 및 이익 성장률:

보수적 기준: 폭발적 성장이 아니더라도, 지난 3~5년간 꾸준히 역성장하지 않았는가?

3. 가치 (Valuation) (가중치: 30점)
   `"수익과 안정성에 비해 주가가 싼가?"에 대한 평가입니다.`

PER (Price-to-Earnings Ratio, 주가수익비율):

보수적 기준: 업종 평균 PER 대비 낮은가? (절대 기준 10 미만)

PBR (Price-to-Book Ratio, 주가순자산비율):

보수적 기준: 1 미만인가? (청산 가치보다 낮게 거래되는가)



## 생각하고 있는 기능들

- '과락' 제도
  - "아무리 다른 게 좋아도 이건 안 된다"는 또 다른 기준을 세울지 고민중. 다음 중 하나라도 해당하면 총점을 무조건 0점 또는 F 등급으로 처리.
    1) 적자 기업: 최근 3년 내 영업이익 적자 또는 당기순이익 적자 2회 이상.
    2) 과다 부채: 부채비율 400% 초과.
    3) 현금흐름 문제: 영업현금흐름 2년 연속 마이너스(-).

