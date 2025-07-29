FROM openjdk:21-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} veco.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "veco.jar"]