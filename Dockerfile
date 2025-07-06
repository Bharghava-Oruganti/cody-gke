# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Package the application
RUN mvn clean install -DskipTests

# Stage 2: Run the app with JRE only
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app


# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/src/main/java/com/example/springweb/files /app/files

EXPOSE 8080
# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
