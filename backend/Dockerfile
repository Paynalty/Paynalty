# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Gradle 래퍼 및 설정 파일 먼저 복사하여 종속성 캐싱 활용
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 의존성 먼저 다운로드 (소스 코드 변경 시에도 캐시 유지)
RUN ./gradlew build -x test --no-daemon || return 0

# 소스 코드 복사 및 실제 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# --- 시간 설정 추가 시작 ---
# Ubuntu 기반이므로 apt-get을 사용하여 tzdata 설치
RUN apt-get update && \
    apt-get install -y tzdata && \
    ln -fs /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    dpkg-reconfigure --frontend noninteractive tzdata && \
    rm -rf /var/lib/apt/lists/*

ENV TZ=Asia/Seoul

# 로그 폴더 생성 및 권한 설정
RUN mkdir -p /var/log/paynalty && chmod 777 /var/log/paynalty

COPY --from=build /app/build/libs/*.jar app.jar

# JVM 옵션 최적화 (G1GC, 메모리 제한 등)
ENTRYPOINT ["java", \
            "-Xms256m", \
            "-Xmx512m", \
            "-XX:+UseG1GC", \
            "-XX:MaxGCPauseMillis=200", \
            "-Dspring.profiles.active=prod", \
            "-jar", "app.jar"]
