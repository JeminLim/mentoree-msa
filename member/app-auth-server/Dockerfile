FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY /home/ec2-user/app/mentoree-webservice/zip/member/app-auth-server/build/libs/member-app-auth-server-1.0.jar member-auth-service.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "member-auth-service.jar"]