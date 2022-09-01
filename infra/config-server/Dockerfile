FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY /home/ec2-user/app/mentoree-webservice/zip/infra/config-server/build/libs/infra-config-server-1.0.jar infra-config-server.jar
ENTRYPOINT ["java", "-jar", "infra-config-server.jar"]