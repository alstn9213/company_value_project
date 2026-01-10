import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/Auth/LoginPage";
import ProtectedRoute from "./components/layout/ProtectedRoute";
import HomePage from "./pages/Home/Homepage";
import SignupPage from "./pages/Auth/SignupPage";
import MainLayout from "./components/layout/MainLayout";
import CompanyListPage from "./pages/Company/CompanyListPage";
import CompanyDetailPage from "./pages/company_detail/CompanyDetailPage";
import WatchlistPage from "./pages/Watchlist/WatchlistPage";

function App() {

  return (
    <BrowserRouter>
      <Routes>
        <Route path='/login' element={<LoginPage />}/>
        <Route path="/signup" element={<SignupPage />} />
        <Route element={<MainLayout />}>
          {/* [공개] 누구나 접근 가능 */}
          <Route path='/' element={<HomePage />}/>
          <Route path='/companies' element={<CompanyListPage />} />
          <Route path='/company/:ticker' element={<CompanyDetailPage />} />
          {/* [비공개] 로그인한 유저만 접근 가능 (관심 종목) */}
          <Route element={<ProtectedRoute />}>
            <Route path='/watchlist' element={<WatchlistPage />} />
          </Route>
        </Route>
        {/* 잘못된 경로는 홈으로 리다이렉트 */}
        <Route path='*' element={<Navigate to='/' replace/>}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App
