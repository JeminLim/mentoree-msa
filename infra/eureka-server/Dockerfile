FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/infra-eureka-server-1.0.jar infra-eureka-server.jar
ENTRYPOINT ["java", "-jar", "infra-eureka-server.jar"]