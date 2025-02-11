# Use JDK image
FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the local machine to the container
COPY build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
