FROM adoptopenjdk/openjdk11
COPY ./target/*.jar service.jar
ENTRYPOINT ["java", "-jar", "service.jar"]
