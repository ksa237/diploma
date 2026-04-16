FROM openjdk:21-ea-slim
WORKDIR app
COPY target/diploma-0.0.1-SNAPSHOT.jar diploma.jar
EXPOSE 8080
CMD ["java", "-jar", "diploma.jar"]
