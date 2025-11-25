# 1. 업계 표준인 Eclipse Temurin (Java 17, Alpine Linux 버전) 사용
FROM eclipse-temurin:17-jdk-alpine

# 2. 빌드된 JAR 파일을 컨테이너 내부로 복사
# (나중에 ./gradlew build 명령어로 생길 파일을 가져온다)
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 3. 컨테이너가 실행될 때 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]