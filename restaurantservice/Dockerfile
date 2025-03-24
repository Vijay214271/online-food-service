FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/restaurantservice-0.0.1-SNAPSHOT.jar /app/restaurantservice.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","restaurantservice.jar"]