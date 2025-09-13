# 1단계: 빌드 환경 (Build Stage)
# Node.js 20 버전 이미지 사용
FROM node:20 AS builder
WORKDIR /app

# 의존성 설치 및 애플리케이션 빌드
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# 2단계: 실행 환경 (Run Stage)
# 가벼운 Nginx 이미지 사용
FROM nginx:alpine

# 1단계에서 빌드된 정적 파일들을 Nginx의 서비스 경로로 복사
COPY --from=builder /app/dist /usr/share/nginx/html

# Nginx가 사용할 포트 지정
EXPOSE 80

# 컨테이너가 시작될 때 Nginx 실행
CMD ["nginx", "-g", "daemon off;"]