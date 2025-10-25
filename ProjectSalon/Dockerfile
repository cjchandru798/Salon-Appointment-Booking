# Use lightweight OpenJDK 21 image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven or Gradle build output
COPY target/*.jar app.jar

# Expose port (Render automatically assigns PORT)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
