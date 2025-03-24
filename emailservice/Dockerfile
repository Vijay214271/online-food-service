FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/emailservice-0.0.1-SNAPSHOT.jar /app/emailservice.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","emailservice.jar"]