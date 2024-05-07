FROM openjdk:21
COPY /target/cloudTest-0.0.1-SNAPSHOT.jar myapp
EXPOSE 8080
CMD ["java", "-jar", "cloudTest.jar"]
