# ====== STAGE 1: Build the Spring Boot app ======
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy Maven config first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the JAR without running tests
RUN mvn clean package -DskipTests

# ====== STAGE 2: Run the Spring Boot app ======
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copy only the final JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (Render uses PORT env variable)
EXPOSE 8080

# Use environment variable PORT if available
ENV PORT=8080

# Run the JAR
ENTRYPOINT ["java","-jar","app.jar"]
