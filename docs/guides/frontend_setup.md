# npm install 목록

- 터미널 창에 명령어 입력:
  `npm install react react-dom react-router-dom @tanstack/react-query zustand axios recharts lucide-react dayjs clsx tailwind-merge && npm install -D typescript vite @vitejs/plugin-react tailwindcss @tailwindcss/postcss postcss eslint eslint-plugin-react-hooks eslint-plugin-react-refresh @typescript-eslint/eslint-plugin @typescript-eslint/parser @types/react @types/react-dom`

## 각 패키지의 용도 설명


### 1. 필수 의존성 설치 (Dependencies)

실제 애플리케이션 구동에 필요한 라이브러리들입니다.

```bash
npm install react react-dom react-router-dom @tanstack/react-query zustand axios recharts lucide-react dayjs clsx tailwind-merge

```

**설치 항목 설명:**

* **Core & Routing:** `react`, `react-dom`, `react-router-dom` 페이지 이동(대시보드 ↔ 기업 상세 ↔ 로그인)을 구현
* **Data Fetching & State:**
* `@tanstack/react-query`: 서버 상태 관리
* `zustand`: 클라이언트 전역 상태 관리
* `axios`: HTTP 클라이언트


* **UI & Visualization:**
* `recharts`: 차트 라이브러리
* `lucide-react`: 아이콘 라이브러리


* **Utils:**
* `dayjs`: 날짜 포맷팅 (설정 파일에 포함됨)
* `clsx`, `tailwind-merge`: Tailwind CSS 클래스 조건부 병합 (UI 컴포넌트 개발 시 필수)



---

### 2. 개발 의존성 설치 (DevDependencies)

빌드, 타입 체크, 린팅, 스타일링(Tailwind v4)에 필요한 라이브러리들입니다.

```bash
npm install -D typescript vite @vitejs/plugin-react tailwindcss @tailwindcss/postcss postcss eslint eslint-plugin-react-hooks eslint-plugin-react-refresh @typescript-eslint/eslint-plugin @typescript-eslint/parser @types/react @types/react-dom

```

**특이 사항 (Tailwind CSS v4):**

* `package.json`을 보면 **Tailwind CSS v4** (`^4.1.17`)를 사용하고 있습니다.
* v4부터는 설정 방식이 달라져 `@tailwindcss/postcss`가 함께 설치되어야 합니다.

### 3. 전체 한 번에 설치하기

위 두 명령어를 합쳐서 한 번에 실행하려면 아래 명령어를 복사해서 터미널에 입력하세요.

```bash
npm install react react-dom react-router-dom @tanstack/react-query zustand axios recharts lucide-react dayjs clsx tailwind-merge && npm install -D typescript vite @vitejs/plugin-react tailwindcss @tailwindcss/postcss postcss eslint eslint-plugin-react-hooks eslint-plugin-react-refresh @typescript-eslint/eslint-plugin @typescript-eslint/parser @types/react @types/react-dom

```

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



