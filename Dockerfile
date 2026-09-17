# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2: Run the app with JRE only
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app


# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar
#COPY --from=build /app/src/main/java/com/example/springweb/files /files

RUN apk add --no-cache docker-cli

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
