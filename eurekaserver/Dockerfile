FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/eurekaserver-0.0.1-SNAPSHOT.jar /app/eurekaserver.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","eurekaserver.jar"]