# ===============================
# EBSA NEXUS BACKEND DOCKERFILE
# Multi-stage build for production optimization
# ===============================

# ===============================
# BUILD STAGE
# ===============================
FROM maven:3.9-eclipse-temurin-17-alpine AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and Maven wrapper for better layer caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copy source code
COPY src ./src

# Build application (skip dependency download step for faster build)
RUN mvn clean package -DskipTests -B

# ===============================
# RUNTIME STAGE
# ===============================
FROM eclipse-temurin:17-jre-alpine

# Create application user for security
RUN addgroup -g 1001 -S appgroup && adduser -S -u 1001 -G appgroup appuser

# Set working directory
WORKDIR /app

# Install curl for health checks (Alpine uses apk)
RUN apk --no-cache add curl

# Copy JAR from build stage
COPY --from=build /app/target/ebsa_nexus-*.jar app.jar

# Change ownership to appuser
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Environment variables with defaults
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=docker

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]