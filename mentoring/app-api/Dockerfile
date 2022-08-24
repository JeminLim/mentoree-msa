FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/mentoring-app-api-1.0.jar mentoring-service.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "mentoring-service.jar"]