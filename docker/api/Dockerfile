FROM adoptopenjdk/openjdk11
EXPOSE 8080
COPY ./target/*.war service.war
ENTRYPOINT ["java", "-jar", "service.war"]
