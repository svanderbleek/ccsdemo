FROM amazoncorretto:27-alpine

WORKDIR /app

COPY target/ccsdemo-0.1.0-standalone.jar app.jar

EXPOSE 3001

ENTRYPOINT ["java", "-jar", "app.jar"]
