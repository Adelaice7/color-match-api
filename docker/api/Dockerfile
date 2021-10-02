FROM adoptopenjdk/openjdk11
EXPOSE 8080
ENV FILE_UPLOAD_DIR=/usr/api-service/res
COPY ./docker/res/ ${FILE_UPLOAD_DIR}
COPY ./target/*.war service.war
ENTRYPOINT ["java", "-jar", "service.war"]
