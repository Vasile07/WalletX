FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace

COPY . .
RUN chmod +x gradlew && ./gradlew :transfer-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring \
    && useradd --system --gid spring --uid 10001 spring

COPY --from=build /workspace/transfer-service/build/libs/transfer-service-*.jar /app/app.jar

ENV SERVER_PORT=8083 \
    DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/walletx \
    DATABASE_USERNAME=walletx \
    DATABASE_PASSWORD=walletx \
    JWT_SECRET="" \
    WALLET_SERVICE_URL=http://host.docker.internal:8082 \
    WALLET_INTERNAL_TRANSFER_TOKEN="" \
    RABBITMQ_HOST=host.docker.internal \
    RABBITMQ_PORT=5672 \
    RABBITMQ_USERNAME=guest \
    RABBITMQ_PASSWORD=guest \
    RABBITMQ_AUTO_STARTUP=true \
    KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
    KAFKA_CONSUMER_GROUP=walletx-transfer-audit

USER spring:spring
EXPOSE 8083

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=5 \
  CMD curl -fsS http://localhost:${SERVER_PORT:-8083}/actuator/health || exit 1

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
