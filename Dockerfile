# Use JDK image
FROM openjdk:17 AS builder

# Install necessary tools
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    xz-utils

# Set the working directory inside the container
WORKDIR /app

# Copy all project files
COPY . .

# Ensure gradlew is executable
RUN chmod +x ./gradlew

# Verify Gradle wrapper
RUN ./gradlew --version

# Build the application
RUN ./gradlew build --stacktrace

# Create final image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]