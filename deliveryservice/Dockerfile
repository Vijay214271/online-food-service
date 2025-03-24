FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/deliveryservice-0.0.1-SNAPSHOT.jar /app/deliveryservice.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","deliveryservice.jar"]