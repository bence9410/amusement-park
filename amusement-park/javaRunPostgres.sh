mvn clean package -DskipTests;
java -jar -Dspring.profiles.active=postgres target/*.jar
