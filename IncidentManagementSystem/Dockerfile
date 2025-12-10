# Stage 1: Build the JAR file using Maven with Java 17
# Use a specific, stable Maven image that includes JDK 17
FROM maven:3.9.6-openjdk-17 AS build
WORKDIR /app
# Copy the entire project context
COPY . .
# Build the executable JAR, skipping tests for faster deployment
RUN mvn clean package -DskipTests

# Stage 2: Create the final, lean runtime image using only JRE 17
# The 'slim' image is smaller and more secure for production use
FROM openjdk:17-jre-slim
WORKDIR /app

# Expose the default Spring Boot port (8080) - optional, but good practice
EXPOSE 8080

# Define the exact JAR file name based on your pom.xml (artifactId-version.jar)
# IncidentManagementSystem-0.0.1-SNAPSHOT.jar
ARG JAR_FILE=target/IncidentManagementSystem-0.0.1-SNAPSHOT.jar
# Copy the compiled JAR from the 'build' stage into the final image
COPY --from=build ${JAR_FILE} app.jar

# The entry point command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
