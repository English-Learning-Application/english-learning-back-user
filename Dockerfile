# Use the official Java 21 image as a base for the build
FROM openjdk:21-jdk-slim as build

# Set the working directory
WORKDIR /app

# Copy the Gradle configuration
COPY ./build.gradle ./build.gradle
COPY ./settings.gradle ./settings.gradle
COPY ./gradlew ./gradlew
COPY ./gradle ./gradle

# Grant execution permissions to gradlew
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies

# Copy the source code
COPY ./src ./src

# Build the application using Gradle
RUN ./gradlew bootJar --no-daemon

# Use another minimal image for runtime with Java 21
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Spring Boot JAR built earlier
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the default application port
EXPOSE 8080

# Set entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]