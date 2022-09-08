FROM openjdk:17-ea-11-jdk-slim
VOLUME /mentoree
COPY apiEncryptionKey.jks apiEncryptionKey.jks
COPY /build/libs/infra-config-server-1.0.jar infra-config-server.jar
ENTRYPOINT ["java", "-jar", "infra-config-server.jar"]