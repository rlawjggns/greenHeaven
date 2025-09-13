# 1단계: 빌드 환경 (Build Stage)
# 자바 17 기반의 가벼운 이미지 사용
FROM openjdk:17-jdk-slim AS builder
# 컨테이너 내부 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드에 필요한 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src ./src

# 애플리케이션 빌드. 테스트는 CI에서 했으므로 제외(-x test)
RUN ./gradlew clean build -x test

# 2단계: 실행 환경 (Run Stage)
# 빌드된 jar 파일만 실행할 가벼운 이미지
FROM openjdk:17-jdk-slim
WORKDIR /app

# 1단계에서 빌드된 jar 파일을 2단계로 복사
COPY --from=builder /app/build/libs/*.jar ./app.jar

# 애플리케이션이 사용할 포트 지정
EXPOSE 1277

# 컨테이너가 시작될 때 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]