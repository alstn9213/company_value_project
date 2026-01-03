# 1. 빌드 단계
FROM node:18-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .

# docker-compose에서 넘겨준 args를 받음
ARG VITE_API_URL
# 환경변수로 설정 (Vite가 빌드할 때 읽을 수 있게)
ENV VITE_API_URL=$VITE_API_URL

RUN npm run build

# 2. 실행 단계 (가볍게 serve 패키지 사용)
FROM node:18-alpine
WORKDIR /app
RUN npm install -g serve
COPY --from=builder /app/dist ./dist

# 5173 포트에서 서비스 실행
CMD ["serve", "-s", "dist", "-l", "5173"]
EXPOSE 5173