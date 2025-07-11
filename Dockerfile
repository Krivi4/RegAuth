# ── Stage 1: сборка ────────────────────────────────────────────
FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q dependency:go-offline        # кэш зависимостей

COPY src ./src
RUN mvn -q clean package -DskipTests    # сборка .jar

# ── Stage 2: рантайм ───────────────────────────────────────────
FROM openjdk:11-jre-slim
WORKDIR /app

# jar → /app/app.jar
COPY --from=build /app/target/RegAuth-*.jar app.jar
COPY .env.properties ./.env.properties

# Порт приложения
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
