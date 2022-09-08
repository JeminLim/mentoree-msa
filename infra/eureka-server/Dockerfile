FROM openjdk:17-ea-11-jdk-slim
VOLUME /mentoree
COPY /build/libs/infra-eureka-server-1.0.jar infra-eureka-server.jar
ENTRYPOINT ["java", "-jar", "infra-eureka-server.jar"]