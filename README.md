# 📈 Value Pick (기업 가치 분석 플랫폼)

- 프로젝트 기간: '2025.11.17 ~ 2025.12.03(2주)'

- 거시경제 지표와 기업 재무 데이터를 결합하여 기업의 투자 가치를 점수화하고 분석하는 웹 애플리케이션입니다.


## 프로젝트 개요
**Value Pick**은 투자자가 객관적인 데이터를 기반으로 의사결정을 내릴 수 있도록 돕는 플랫폼입니다.
단순한 주가 확인을 넘어, FRED(연준 경제 데이터) API를 통한 거시경제 흐름 파악과 기업의 재무제표 분석, 그리고 이를 종합한 자체 알고리즘(S~D 등급)을 통해 기업의 건전성과 투자 매력도를 시각적으로 제공합니다.

### 배포 및 데모

- **Web URL:** https://valuepick.p-e.kr

> **⚠️ 서비스 이용 안내 (비용 최적화 정책)**
> 본 프로젝트는 포트폴리오 용도로 운영되며, **GCP Free Tier:** 인스턴스 사양 제한으로 초기 로딩 시 지연이 발생할 수 있습니다.

## 시연

![alt text](images/거시경제차트.gif)
<br/>
장단기 금리차 역전 구간을 그래프에서 빨간 박스로 표시해 미국의 경제 상황을 직관적으로 알 수 있습니다.
<br/>
<br/>

![alt text](images/재무제표툴팁.gif)
<br/>
기업 상세 페이지로 들어가면, 해당 기업의 재무제표, 기업 점수, 주가 그래프 등을 볼 수 있습니다.
<br/>
<br/>

![alt text](images/관심목록.gif)
<br/>
관심있는 기업을 추가하면 마이페이지에서 따로 관리 종목들을 확인할 수 있습니다.

## 🛠 기술 스택 (Tech Stack)

### Frontend
| 구분 | 기술 (Version / Library) |
| :-- | :-- |
| **Framework** | React 18, Vite |
| **Language** | TypeScript |
| **Styling** | Tailwind CSS |
| **State Mngt** | Zustand |
| **HTTP Client** | Axios |
| **Charting** | Recharts |

### Backend
| 구분 | 기술 (Version / Library) |
| :-- | :-- |
| **Framework** | Spring Boot 3.5.7 |
| **Language** | Java 17 |
| **Database** | MySQL 8.0, Redis |
| **ORM** | Spring Data JPA |
| **Security** | Spring Security, JWT |
| **API Docs** | Swagger (Springdoc OpenAPI) |
| **Data Collection** | Python, WebClient |

### DevOps & Infrastructure
| 구분 | 기술 |
| :-- | :-- |
| **Server** | GCP (Google Cloud Platform) EC2 (Linux) |
| **Container** | Docker, Docker Compose |
| **Proxy** | Nginx (Reverse Proxy, SSL/HTTPS 적용) |
| **CI/CD** | GitHub Actions |

## ✨ 주요 기능 (Key Features)

### 📊 1. 거시경제(Macro) 분석
- **FRED API 연동**: 금리, GDP, CPI, 실업률 등 주요 경제 지표를 실시간으로 수집 및 시각화.
- **경제 흐름 파악**: 과거부터 현재까지의 지표 변동 추이를 그래프로 제공하여 시장 상황 판단 보조.

### 🏢 2. 기업 분석 및 스코어링
- **재무제표 분석**: 손익계산서, 대차대조표, 현금흐름표 데이터를 시각화.
- **투자 등급 산정**: 
    - 성장성, 수익성, 안정성, 밸류에이션 4가지 지표를 종합 분석.
    - **Macro Penalty System**: 거시경제 상황(고금리 등)에 따라 기업 점수에 페널티를 부여하는 동적 알고리즘 적용.
    - S등급부터 D등급까지 직관적인 투자 등급 제공.
- **레이더 차트**: 기업의 강점과 약점을 한눈에 파악할 수 있는 육각형 차트 제공.

### 📈 주가 정보
- 실시간/과거 주가 차트 제공.
- 캔들 차트 및 거래량 분석.

## API Reference
![alt text](image.png)
![alt text](image-1.png)
## 👨‍💻 Developer

**alstn9213**
<br> - GitHub: https://github.com/alstn9213
<br> - Velog: https://velog.io/@kms0425/posts
<br> - Email: alstn9213@naver.com


