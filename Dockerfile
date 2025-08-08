# 1단계: Build Stage (원본 유지)
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build -x test

# 2단계: Runtime Stage (OTel 에이전트 추가)
FROM openjdk:17-jdk-slim
WORKDIR /app

# 👉 OpenTelemetry Java Agent 다운로드 (curl 설치 없이 ADD로 원격 가져오기)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

# 빌드 산출물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 👉 기본 OTEL 설정 (compose에서 덮어쓰기 가능)
ENV OTEL_TRACES_EXPORTER=otlp \
    OTEL_EXPORTER_OTLP_ENDPOINT=http://jaeger:4318 \
    OTEL_METRICS_EXPORTER=none \
    OTEL_LOGS_EXPORTER=none \
    OTEL_RESOURCE_ATTRIBUTES=service.name=my-service,deployment.environment=docker \
    JAVA_TOOL_OPTIONS="-javaagent:/otel/opentelemetry-javaagent.jar"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
