FROM gradle:8.10.2-jdk21 AS build
WORKDIR /workspace

COPY . .
RUN chmod +x gradlew && ./gradlew :user-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring \
    && useradd --system --gid spring --uid 10001 spring

COPY --from=build /workspace/user-service/build/libs/user-service-*.jar /app/app.jar

ENV SERVER_PORT=8081 \
    DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/walletx \
    DATABASE_USERNAME=walletx \
    DATABASE_PASSWORD=walletx \
    JWT_SECRET="" \
    JWT_EXPIRATION_MS=3600000

USER spring:spring
EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=5 \
  CMD curl -fsS http://localhost:${SERVER_PORT:-8081}/actuator/health || exit 1

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
