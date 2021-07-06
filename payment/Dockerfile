FROM gradle:7.0.2-jdk8 as build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM openjdk:8-jdk-alpine
EXPOSE 8086
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]