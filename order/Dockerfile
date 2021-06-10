FROM openjdk:8-jdk-alpine
EXPOSE 8084
VOLUME /tmp
ARG JAR_FILE=build/libs/order-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]