FROM eclipse-temurin:21-jre-alpine-3.23
ARG JAR_FILE=../web-api/target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]