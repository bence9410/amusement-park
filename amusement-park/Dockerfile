FROM openjdk:11.0.7-jre-slim
ADD target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java -jar app.jar --spring.profiles.active=docker" ]