<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Value Investment Project</title>
    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #0f172a; /* Dark Slate */
            color: #e2e8f0;
        }
        .gradient-text {
            background: linear-gradient(135deg, #60a5fa, #34d399);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .card {
            background: rgba(30, 41, 59, 0.7);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.1);
            transition: transform 0.3s ease;
        }
        .card:hover {
            transform: translateY(-5px);
            border-color: #34d399;
        }
        .tech-badge {
            background: rgba(51, 65, 85, 0.5);
            border: 1px solid #475569;
            padding: 0.25rem 0.75rem;
            border-radius: 9999px;
            font-size: 0.875rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
    </style>
</head>
<body class="antialiased overflow-x-hidden">

    <!-- Header / Hero Section -->
    <header class="relative w-full min-h-screen flex flex-col justify-center items-center text-center px-4 overflow-hidden">
        <!-- Background Effect -->
        <div class="absolute top-0 left-0 w-full h-full overflow-hidden -z-10">
            <div class="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-blue-600 rounded-full mix-blend-multiply filter blur-[120px] opacity-20 animate-blob"></div>
            <div class="absolute top-[10%] right-[-10%] w-[40%] h-[40%] bg-emerald-500 rounded-full mix-blend-multiply filter blur-[120px] opacity-20 animate-blob animation-delay-2000"></div>
            <div class="absolute bottom-[-20%] left-[20%] w-[40%] h-[40%] bg-purple-600 rounded-full mix-blend-multiply filter blur-[120px] opacity-20 animate-blob animation-delay-4000"></div>
        </div>

        <div class="max-w-4xl mx-auto space-y-6">
            <div class="inline-block px-4 py-1.5 rounded-full border border-emerald-500/30 bg-emerald-500/10 text-emerald-400 text-sm font-medium mb-4">
                Personal Project 2025
            </div>
            <h1 class="text-5xl md:text-7xl font-bold tracking-tight leading-tight">
                기업 가치 평가 &<br>
                <span class="gradient-text">거시 경제 분석 플랫폼</span>
            </h1>
            <p class="text-xl text-slate-400 max-w-2xl mx-auto">
                재무제표 데이터와 거시 경제 지표를 결합하여<br>
                "위험한 기업을 회피하고, 지속 가능한 투자"를 돕는 백엔드 시스템
            </p>
            
            <div class="flex flex-wrap justify-center gap-4 mt-8">
                <a href="#features" class="px-8 py-3 rounded-lg bg-blue-600 hover:bg-blue-500 text-white font-semibold transition shadow-lg shadow-blue-600/20">
                    주요 기능 보기
                </a>
                <a href="#architecture" class="px-8 py-3 rounded-lg bg-slate-700 hover:bg-slate-600 text-white font-semibold transition border border-slate-600">
                    시스템 아키텍처
                </a>
            </div>
        </div>
        
        <!-- Scroll Down Indicator -->
        <div class="absolute bottom-10 animate-bounce text-slate-500">
            <i class="fas fa-chevron-down text-2xl"></i>
        </div>
    </header>

    <!-- Tech Stack Section -->
    <section id="tech-stack" class="py-16 border-y border-slate-800 bg-slate-900/50">
        <div class="max-w-6xl mx-auto px-4">
            <h2 class="text-center text-2xl font-semibold mb-10 text-slate-300">Built With</h2>
            <div class="flex flex-wrap justify-center gap-4 md:gap-8">
                <div class="tech-badge text-orange-400"><i class="fab fa-java text-xl"></i> Java 17</div>
                <div class="tech-badge text-green-400"><i class="fas fa-leaf text-xl"></i> Spring Boot 3.x</div>
                <div class="tech-badge text-blue-400"><i class="fas fa-database text-xl"></i> MariaDB</div>
                <div class="tech-badge text-purple-400"><i class="fas fa-code text-xl"></i> Spring Data JPA</div>
                <div class="tech-badge text-yellow-400"><i class="fas fa-bolt text-xl"></i> WebClient (Async)</div>
                <div class="tech-badge text-cyan-400"><i class="fab fa-react text-xl"></i> React (Planned)</div>
            </div>
        </div>
    </section>

    <!-- Key Features Section -->
    <section id="features" class="py-24 bg-slate-900">
        <div class="max-w-6xl mx-auto px-4">
            <div class="text-center mb-16">
                <h2 class="text-3xl md:text-4xl font-bold mb-4">Core Features</h2>
                <p class="text-slate-400">정량적 재무 분석에 시장의 흐름을 더했습니다.</p>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                <!-- Feature 1 -->
                <div class="card p-8 rounded-2xl relative overflow-hidden group">
                    <div class="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition">
                        <i class="fas fa-chart-line text-9xl text-blue-500"></i>
                    </div>
                    <div class="w-12 h-12 bg-blue-500/20 rounded-lg flex items-center justify-center mb-6 text-blue-400">
                        <i class="fas fa-calculator text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-bold mb-3">재무 건전성 스코어링</h3>
                    <p class="text-slate-400 text-sm leading-relaxed mb-4">
                        안정성(40), 수익성(30), 가치(20), 미래투자(10)의 가중치를 적용하여 기업을 100점 만점으로 평가합니다.
                    </p>
                    <ul class="text-slate-500 text-sm space-y-2">
                        <li><i class="fas fa-check text-blue-500 mr-2"></i>부채비율 & 유동비율 분석</li>
                        <li><i class="fas fa-check text-blue-500 mr-2"></i>ROE & 영업이익률 평가</li>
                        <li><i class="fas fa-check text-blue-500 mr-2"></i>R&D 투자 적극성 가산점</li>
                    </ul>
                </div>

                <!-- Feature 2 -->
                <div class="card p-8 rounded-2xl relative overflow-hidden group">
                    <div class="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition">
                        <i class="fas fa-globe-americas text-9xl text-emerald-500"></i>
                    </div>
                    <div class="w-12 h-12 bg-emerald-500/20 rounded-lg flex items-center justify-center mb-6 text-emerald-400">
                        <i class="fas fa-university text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-bold mb-3">거시 경제 대시보드</h3>
                    <p class="text-slate-400 text-sm leading-relaxed mb-4">
                        FRED API를 통해 미 연준(Fed)의 주요 경제 지표를 실시간으로 수집하고 시각화합니다.
                    </p>
                    <ul class="text-slate-500 text-sm space-y-2">
                        <li><i class="fas fa-check text-emerald-500 mr-2"></i>10년물 국채 금리 모니터링</li>
                        <li><i class="fas fa-check text-emerald-500 mr-2"></i>장단기 금리차 역전 감지</li>
                        <li><i class="fas fa-check text-emerald-500 mr-2"></i>실업률 & 인플레이션(CPI) 추적</li>
                    </ul>
                </div>

                <!-- Feature 3 -->
                <div class="card p-8 rounded-2xl relative overflow-hidden group">
                    <div class="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition">
                        <i class="fas fa-balance-scale text-9xl text-red-500"></i>
                    </div>
                    <div class="w-12 h-12 bg-red-500/20 rounded-lg flex items-center justify-center mb-6 text-red-400">
                        <i class="fas fa-exclamation-triangle text-2xl"></i>
                    </div>
                    <h3 class="text-xl font-bold mb-3">동적 페널티 시스템</h3>
                    <p class="text-slate-400 text-sm leading-relaxed mb-4">
                        단순 재무 점수가 높아도, 시장 상황이 좋지 않으면 점수를 차감하여 "보수적 투자"를 유도합니다.
                    </p>
                    <ul class="text-slate-500 text-sm space-y-2">
                        <li><i class="fas fa-check text-red-500 mr-2"></i>경기 침체 시그널 발생 시 감점</li>
                        <li><i class="fas fa-check text-red-500 mr-2"></i>고금리 시기 부채 과다 기업 경고</li>
                        <li><i class="fas fa-check text-red-500 mr-2"></i>자본잠식 등 과락(F등급) 처리</li>
                    </ul>
                </div>
            </div>
        </div>
    </section>

    <!-- Architecture Section -->
    <section id="architecture" class="py-24 bg-slate-800/50">
        <div class="max-w-5xl mx-auto px-4">
            <div class="flex flex-col md:flex-row gap-12 items-center">
                <div class="flex-1 space-y-6">
                    <h2 class="text-3xl font-bold">System Architecture</h2>
                    <p class="text-slate-400">
                        대용량 금융 데이터 처리를 위해 <strong>Spring WebClient</strong>를 활용한 비동기 파이프라인을 구축했습니다.
                    </p>
                    
                    <div class="space-y-4">
                        <div class="flex items-start gap-4">
                            <div class="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center shrink-0 font-bold text-blue-400">1</div>
                            <div>
                                <h4 class="font-semibold text-slate-200">Data Collection</h4>
                                <p class="text-sm text-slate-400">Scheduler가 주기적으로 Alpha Vantage(기업)와 FRED(경제) API를 호출하여 최신 데이터를 수집합니다.</p>
                            </div>
                        </div>
                        <div class="flex items-start gap-4">
                            <div class="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center shrink-0 font-bold text-blue-400">2</div>
                            <div>
                                <h4 class="font-semibold text-slate-200">Data Processing</h4>
                                <p class="text-sm text-slate-400">수집된 JSON 데이터를 파싱하여 DB에 적재하고, 결측치 보정 및 날짜 동기화 작업을 수행합니다.</p>
                            </div>
                        </div>
                        <div class="flex items-start gap-4">
                            <div class="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center shrink-0 font-bold text-blue-400">3</div>
                            <div>
                                <h4 class="font-semibold text-slate-200">Scoring Engine</h4>
                                <p class="text-sm text-slate-400">ScoringService가 재무 지표와 거시 경제 페널티를 계산하여 최종 투자 등급(S~F)을 산출합니다.</p>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Diagram Representation -->
                <div class="flex-1 w-full">
                    <div class="bg-slate-900 p-6 rounded-xl border border-slate-700 shadow-2xl">
                        <div class="flex flex-col gap-4 text-center text-sm font-mono">
                            <div class="flex justify-between gap-4">
                                <div class="bg-slate-800 p-3 rounded border border-slate-600 flex-1">Alpha Vantage API</div>
                                <div class="bg-slate-800 p-3 rounded border border-slate-600 flex-1">FRED Economic API</div>
                            </div>
                            <div class="text-slate-500"><i class="fas fa-arrow-down"></i> JSON Data</div>
                            <div class="bg-blue-900/30 p-4 rounded border border-blue-500/30">
                                <div class="font-bold text-blue-300 mb-2">Spring Boot Server</div>
                                <div class="grid grid-cols-2 gap-2">
                                    <div class="bg-slate-800 p-2 rounded">Scheduler</div>
                                    <div class="bg-slate-800 p-2 rounded">WebClient</div>
                                    <div class="bg-emerald-900/30 p-2 rounded col-span-2 border border-emerald-500/30">Scoring Service</div>
                                </div>
                            </div>
                            <div class="text-slate-500"><i class="fas fa-arrow-down"></i> JDBC</div>
                            <div class="bg-slate-800 p-3 rounded border border-slate-600">
                                <i class="fas fa-database mr-2"></i>MariaDB (Company / Macro / Score)
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="py-8 text-center text-slate-500 text-sm border-t border-slate-800">
        <p>&copy; 2025 Company Value Project. All rights reserved.</p>
        <div class="mt-4 space-x-4">
            <a href="https://github.com/alstn9213" target="_blank" class="hover:text-white transition"><i class="fab fa-github text-xl"></i></a>
        </div>
    </footer>

</body>
</html>
