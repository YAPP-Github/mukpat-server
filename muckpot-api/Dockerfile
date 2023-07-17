FROM openjdk:11

EXPOSE 8080

ARG JAR_FILE=build/libs/*.jar
ARG PROFILE
COPY ${JAR_FILE} app.jar

ENV PROFILE=${PROFILE}

ENTRYPOINT ["java", "-Dspring.profiles.active=${PROFILE}", "-Duser.timezone=Asia/Seoul", "-jar", "/app.jar"]