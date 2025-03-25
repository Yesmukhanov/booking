# Use JDK image
FROM openjdk:17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Make gradlew executable
RUN chmod +x ./gradlew

# Install findutils to provide xargs
RUN apt-get update && apt-get install -y findutils

# Build the application
RUN ./gradlew build

# Create final image
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
