FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY /infra/api-gateway-service/build/libs/infra-api-gateway-service-1.0.jar infra-api-gateway-service.jar
ENTRYPOINT ["java", "-jar", "infra-api-gateway-service.jar"]