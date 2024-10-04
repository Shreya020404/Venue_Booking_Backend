# Use a base image with Java
FROM openjdk:17-jdk-slim AS builder

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src ./src

# Give execute permissions to the Gradle wrapper
RUN chmod +x gradlew

# Build the application
RUN ./gradlew clean build -x test

# Second stage: Create a smaller image for the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the jar file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
