import './App.css'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import LoginPage from './pages/Auth/LoginPage'
import ProtectedRoute from './components/layout/ProtectedRoute'
import HomePage from './pages/Home/Homepage';
import SignupPage from './pages/Auth/SignupPage';
import MainLayout from './components/layout/MainLayout';

function App() {

  return (
    <BrowserRouter>
      <Routes>
        {/* 공개 라우트 (로그인 불필요) */}
        <Route path='/login' element={<LoginPage />}/>
        <Route path="/signup" element={<SignupPage />} />

        {/* 보호된 라우트 (로그인 필요) */}
        <Route element={<ProtectedRoute />}>
          {/* MainLayout으로 감싸서 사이드바 공통 적용 */}
          <Route element={<MainLayout />}>
            {/* 1. 대시보드 */}
            <Route path='/' element={<HomePage />}/>

            {/* 2. 기업 찾기 (추후 구현 예정) */}
            <Route path='/companies' element={
              <div className="text-slate-300 p-10 text-center">
                <h2 className="text-2xl font-bold mb-4">🏢 기업 찾기</h2>
                <p>준비 중입니다. 곧 기업 목록과 검색 기능이 추가됩니다.</p>
              </div>
            } />
            {/* 3. 관심 종목 (추후 구현 예정) */}
            <Route path='/watchlist' element={
              <div className="text-slate-300 p-10 text-center">
                <h2 className="text-2xl font-bold mb-4">⭐ 관심 종목</h2>
                <p>내가 찜한 기업들을 모아보는 페이지입니다.</p>
              </div>
            } />

             {/* 4. 기업 상세 (추후 구현 예정) */}
            <Route path='/company/:ticker' element={
              <div className="text-slate-300 p-10 text-center">
                <h2 className="text-2xl font-bold mb-4">📊 기업 상세 분석</h2>
                <p>선택한 기업의 재무 점수와 차트를 보여줍니다.</p>
              </div>
            } />
            </Route>
        </Route>

        {/* 잘못된 경로는 홈으로 리다이렉트 */}
        <Route path='*' element={<Navigate to='/' replace/>}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App
