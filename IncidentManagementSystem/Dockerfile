# ---------------------------------------------------------
# Stage 1: Build the JAR file using Maven + Java 17
# ---------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy project files
COPY . .

# Build the Spring Boot JAR (skip tests for faster builds)
RUN mvn clean package -DskipTests


# ---------------------------------------------------------
# Stage 2: Run the application using a minimal JRE 17 image
# ---------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Spring Boot default port (optional)
EXPOSE 8080

# Your JAR file name — automatically pulled from build stage
ARG JAR_FILE=target/IncidentManagementSystem-0.0.1-SNAPSHOT.jar

# Copy the built JAR into this image
COPY --from=build ${JAR_FILE} app.jar

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
