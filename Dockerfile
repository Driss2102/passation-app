# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy POM first so Maven can download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# SPRING_PROFILES_ACTIVE est défini par Render via envVars (dev/homol/prod)
ENTRYPOINT ["java", "-jar", "app.jar"]
