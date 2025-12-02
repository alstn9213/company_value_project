# npm install 목록

- 터미널 창에 명령어 입력:
  `npm install axios @tanstack/react-query zustand react-router-dom recharts lucide-react clsx tailwind-merge dayjs`

## 각 패키지의 용도 설명

- axios: 백엔드(/api/\*\*)와 HTTP 통신을 위해 사용합니다. (Interceptor로 JWT 토큰 자동 처리)

- @tanstack/react-query: 서버 데이터(거시 경제 지표, 기업 정보) 캐싱 및 로딩 상태 관리를 위해 필수적입니다.

- zustand: 로그인 유저 정보(Auth) 등 전역 상태를 관리하는 가벼운 라이브러리입니다.

- react-router-dom: 페이지 이동(대시보드 ↔ 기업 상세 ↔ 로그인)을 구현합니다.

- recharts: 거시 경제 지표(LineChart)와 기업 점수(RadarChart)를 그리기 위한 차트 라이브러리입니다.

- lucide-react: 깔끔한 아이콘 팩입니다. (기존 index.html의 FontAwesome 대신 React 환경에서 더 가볍게 사용 추천)

- clsx, tailwind-merge: Tailwind CSS 사용 시 조건부 스타일링(동적 클래스)을 쉽게 합쳐주는 유틸리티입니다.

- dayjs: 거시 경제 지표의 날짜(recordedDate) 포맷팅을 위해 moment.js보다 가벼운 라이브러리를 추천합니다.

## 스타일링 설정 (Tailwind CSS)

- 패키지 설치
   npm install -D @tailwindcss/postcss

- 루트 프로젝트에 tailwind.config.js 파일 생성

```js
/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      // 백엔드 index.html에 있던 컬러 테마 이식
      colors: {
        dark: "#0f172a",
        card: "rgba(30, 41, 59, 0.7)",
      },
    },
  },
  plugins: [],
};
```

- 루트 프로젝트에 postcss.config.js 파일 생성
```js
export default {
  plugins: {
    '@tailwindcss/postcss': {},
    autoprefixer: {},
  },
}
```

# any 타입 사용 지양
타입으로 any를 할당하면 ts를 쓰는 의미가 없으므로 any는 사용하지 않는다.