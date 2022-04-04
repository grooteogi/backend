FROM openjdk:11-jdk

VOLUME /tmp
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} api.jar
ENTRYPOINT ["java", "-jar", "/api.jar"]
