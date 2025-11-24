import './App.css'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import LoginPage from './pages/Auth/LoginPage'
import ProtectedRoute from './components/layout/ProtectedRoute'
import HomePage from './pages/Home/Homepage';
import SignupPage from './pages/Auth/SignupPage';
import MainLayout from './components/layout/MainLayout';
import CompanyListPage from './pages/CompanyList/CompantListPage';
import CompanyDetailPage from './pages/CompanyDetail/CompanyDetailPage';
import WatchlistPage from './pages/Watchlist/WatchlistPage';

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

            {/* 2. 기업 찾기 */}
            <Route path='/companies' element={<CompanyListPage />} />

            {/* 3. 관심 종목  */}
            <Route path='/watchlist' element={<WatchlistPage />} />

             {/* 4. 기업 상세 */}
            <Route path='/company/:ticker' element={<CompanyDetailPage />} />
            </Route>
        </Route>

        {/* 잘못된 경로는 홈으로 리다이렉트 */}
        <Route path='*' element={<Navigate to='/' replace/>}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App
