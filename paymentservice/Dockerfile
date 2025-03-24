FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/paymentservice-0.0.1-SNAPSHOT.jar /app/paymentservice.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","paymentservice.jar"]