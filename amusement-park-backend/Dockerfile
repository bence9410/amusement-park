FROM amazoncorretto:26-headless
ADD target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java -jar app.jar --spring.profiles.active=docker" ]