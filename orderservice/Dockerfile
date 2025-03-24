FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/orderservice-0.0.1-SNAPSHOT.jar /app/orderservice.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","orderservice.jar"]