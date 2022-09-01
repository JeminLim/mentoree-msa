FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY /home/ec2-user/app/mentoree-webservice/zip/reply/app-api/build/libs/reply-app-api-1.0.jar reply-service.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "reply-service.jar"]