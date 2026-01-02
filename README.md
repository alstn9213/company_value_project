<div align="center">

# 📈 기업 가치 평가 & 거시 경제 분석 플랫폼 (Value Pick)

**"시장(Macro)과 기업(Micro)을 결합한 투자 정보 제공"**

</div>

- 기업 재무제표와 거시 경제 지표(금리, 실업률 등)를 종합 분석하여 장기 투자 적합성을 평가하는 풀스택 웹 애플리케이션입니다.

- 프로젝트 기간: '2025.11.17 ~ 2025.12.03(2주)'

## 🎯 기획 의도

- **보수적 투자 가이드:** 재무적으로 탄탄한 기업도 경기 침체기에는 위험할 수 있다는 점에 착안해서 거시 경제 지표를 평가 로직에 반영했습니다.
- **리스크 관리:** 부채가 과도하거나 자본 잠식 상태인 기업을 필터링하여 안전한 투자처를 제안합니다.

## 배포 및 데모

- **Web URL:** http://valuepick.p-e.kr
- **Demo Video:** `[유튜브 링크 업데이트 예정]`

> **⚠️ 서비스 이용 안내 (비용 최적화 정책)**
> 본 프로젝트는 포트폴리오 용도로 운영되며, **GCP Free Tier:** 인스턴스 사양 제한으로 초기 로딩 시 지연이 발생할 수 있습니다.

## 🛠 기술 스택 (Tech Stack)

### Backend

<img src="https://img.shields.io/badge/Java-17-orange?logo=java&style=flat-square" /> <img src="https://img.shields.io/badge/Spring%20Boot-3.5-green?logo=springboot&style=flat-square" /> <img src="https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&style=flat-square" /> 
<br>
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-gray?logo=spring&style=flat-square" /> <img src="https://img.shields.io/badge/Spring%20WebClient-gray?logo=spring&style=flat-square" />

### Frontend

<img src="https://img.shields.io/badge/React-18-blue?logo=react&style=flat-square" /> <img src="https://img.shields.io/badge/TypeScript-5.x-3178C6?logo=typescript&style=flat-square" /> <img src="https://img.shields.io/badge/Vite-Build-646CFF?logo=vite&style=flat-square" /> 
<br>
<img src="https://img.shields.io/badge/Tailwind_CSS-4.0-38B2AC?logo=tailwindcss&style=flat-square" /> <img src="https://img.shields.io/badge/Zustand-State-orange?logo=react&style=flat-square" />

### Data & Infrastructure

<img src="https://img.shields.io/badge/MariaDB-10.6-003545?logo=mariadb&style=flat-square" /> <img src="https://img.shields.io/badge/Redis-Cache-red?logo=redis&style=flat-square" /> 
<br>
<img src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&style=flat-square" /> <img src="https://img.shields.io/badge/GCP-Compute_Engine-4285F4?logo=googlecloud&style=flat-square" />



## 💡 핵심 기능

### 1. 📊 기업 재무 건전성 스코어링

각 기업의 재무제표를 분석하여 4대 지표를 산출하고 100점 만점의 점수를 매깁니다.

- **안정성 (40점):** 부채비율(금융업/비금융업 차등 적용), 영업활동 현금흐름
- **수익성 (30점):** ROE, 영업이익률
- **가치 (20점):** PER, PBR 기반 수익 대비 주가 분석
- **미래 투자 (10점):** 매출액 대비 R&D 및 CapEx 비중

### 2. ⚠️ 동적 페널티 시스템 (Dynamic Penalty)

시장 리스크에 따라 기업 점수가 실시간으로 변동됩니다. 

- **경기 침체 경고 (-10점):** 장단기 금리차(10Y-2Y)는 장기 채권과 단기 채권 이자율 차이로, 평상시에는 장기 채권 이자율이 높지만 경제의 앞날이 불투명할 경우 단기 채권 이자율이 높아지는 현상이 발생합니다. 이 때문에 장단기 금리 역전은 경제 침체의 징조로 여겨지며, 이런 상황에서의 주식 투자는 위험도가 높으므로 모든 기업 점수를 -10점 깎습니다.
- **고금리 상황에서 부채 비율이 높은 기업 (-20점):** 금리가 높을 경우 부채가 많은 기업은 이자 비용이 상승합니다.이럴 경우 부도 확률이 급격히 올라가므로 금리가 높은(4% 이상) 상황에서 부채가 많은(부채 비율 200% 이상) 기업은 감점(-10점)하고 빚이 더 늘어나는 경우(설비 투자, R&D 등) 추가로 감점(-10점)합니다.

### 3. 🌏 거시 경제 대시보드

- 매일 새벽 1시 업데이트되는 금리, 인플레이션, 실업률 추이 제공.
- **Red Zone Alert:** 차트 내 장단기 금리차 역전 구간을 붉게 표시하여 위험 경고.


## 🚀 실행 방법 (Local)

**Docker Compose**를 통해 DB(MariaDB, Redis)와 애플리케이션을 한 번에 실행할 수 있습니다.

### 1. 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성합니다.

```env
DB_ROOT_PASSWORD=your_password
DB_NAME=value
```

### 2. 컨테이너 실행

```bash
# 백엔드 및 DB 실행
docker-compose up -d --build
```

### 3. 프론트엔드 실행

```bash
cd FRONT/companyvalue
npm install
npm run dev
```

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`

## API Reference
![alt text](images/image.png)
![alt text](images/image-1.png)
---


## 👨‍💻 Developer

**alstn9213**
<br> - GitHub: https://github.com/alstn9213
<br> - Velog: https://velog.io/@kms0425/posts
<br> - Email: alstn9213@naver.com
