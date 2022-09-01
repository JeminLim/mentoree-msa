FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY /home/ec2-user/app/mentoree-webservice/zip/member/app-api/build/libs/member-app-api-1.0.jar member-service.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "member-service.jar"]