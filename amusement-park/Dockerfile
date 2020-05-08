FROM openjdk:8-jdk-alpine
ADD target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java -jar app.jar --spring.profiles.active=docker" ]