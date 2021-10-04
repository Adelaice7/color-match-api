FROM adoptopenjdk/openjdk11
EXPOSE 8080
ENV DOCKER_FILE_RES_DIR=/usr/api-service/res
COPY ./docker/res/ ${DOCKER_FILE_RES_DIR}
COPY ./target/*.war service.war
ENTRYPOINT ["java", "-jar", "service.war"]
