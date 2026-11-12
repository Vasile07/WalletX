FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace

COPY . .
RUN chmod +x gradlew && ./gradlew :api-gateway:bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring \
    && useradd --system --gid spring --uid 10001 spring

COPY --from=build /workspace/api-gateway/build/libs/api-gateway-*.jar /app/app.jar

ENV SERVER_PORT=8080 \
    JWT_SECRET="" \
    USER_SERVICE_URL=http://host.docker.internal:8081 \
    WALLET_SERVICE_URL=http://host.docker.internal:8082 \
    TRANSFER_SERVICE_URL=http://host.docker.internal:8083 \
    WALLET_LOW_BALANCE_NOTIFICATION_TOKEN=""

USER spring:spring
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=5 \
  CMD curl -fsS http://localhost:${SERVER_PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
