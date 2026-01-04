import { Outlet } from "react-router-dom";
import Header from "./Header";

const MainLayout = () => {
  return (
    <div className="min-h-screen bg-[#0f172a] text-slate-200 font-sans flex flex-col">
      <Header />
      <main className="flex-1 w-full relative">
        <div className="absolute top-0 left-0 w-full h-[300px] bg-gradient-to-b from-blue-900/5 to-transparent pointer-events-none" />
        <div className="relative z-0 w-full p-6 lg:p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default MainLayout;