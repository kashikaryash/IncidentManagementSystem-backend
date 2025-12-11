# ---------------------------------------------------------
# Stage 1: Build the JAR file using Maven + Java 17
# ---------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests


# ---------------------------------------------------------
# Stage 2: Run the application using a minimal JRE 17 image
# ---------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
EXPOSE 8080

# Copy ANY jar produced from the build stage
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
