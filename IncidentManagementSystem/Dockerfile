# Stage 1: Build the application
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (cache-friendly)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Package the Spring Boot application as a fat jar
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]
