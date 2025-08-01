# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build

# Install necessary packages for building (Alpine Linux)
RUN apk add --no-cache \
    wget \
    unzip \
    curl

# Set working directory
WORKDIR /app

# Copy gradle wrapper and build files
COPY homecare/gradle ./gradle
COPY homecare/gradlew ./
COPY homecare/gradle.properties* ./
COPY homecare/build.gradle ./
COPY homecare/settings.gradle ./

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies (for better caching)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY homecare/src ./src

# Build the application
RUN ./gradlew bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install curl for health check (Alpine Linux) - BEFORE user switch
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Create non-root user for security (Alpine Linux)
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to spring user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# Expose port (Spring Boot default is 8080)
EXPOSE 8080

# Set JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:+UseG1GC -XX:MaxRAMPercentage=75.0"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
