# Use a lightweight OpenJDK image
FROM eclipse-temurin:21-jre-alpine

# Set work directory
WORKDIR /app

# Copy the built JAR into the image
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
