mvn clean package -DskipTests;
java -Dserver.port=5000 -jar target/*.jar;
