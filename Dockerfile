FROM openjdk:17
WORKDIR /app
COPY /target/cloudTest-0.0.1-SNAPSHOT.jar myapp.jar
EXPOSE 8080
CMD ["java", "-jar", "myapp.jar"]