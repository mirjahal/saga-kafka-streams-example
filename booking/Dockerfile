FROM openjdk:8-jdk-alpine
EXPOSE 8085
ARG JAR_FILE=build/libs/booking-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]