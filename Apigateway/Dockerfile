# Use OpenJDK as base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/Apigateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar

# Expose the port the API Gateway listens on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]
