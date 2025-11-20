import './App.css'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import LoginPage from './pages/Auth/LoginPage'
import ProtectedRoute from './components/layout/ProtectedRoute'
import HomePage from './pages/Home/Homepage';

function App() {

  return (
    <BrowserRouter>
      <Routes>
        {/* 공개 라우트 */}
        <Route path='/login' element={<LoginPage />}/>

        {/* 보호된 라우트 (로그인 필요) */}
        <Route element={<ProtectedRoute />}>
          <Route path='/' element={<HomePage />}/>
          <Route path='/watchlist' element={<div>관심 종목(Watchlist)</div>}/>
          <Route path='/company/:ticker' element={<div>기업 상세(Detail)</div>}/>
        </Route>

        {/* 잘못된 경로는 홈으로 리다이렉트 */}
        <Route path='*' element={<Navigate to='/' replace/>}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App
