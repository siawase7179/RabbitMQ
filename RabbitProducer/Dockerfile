FROM openjdk:8-jdk-alpine

COPY target/*SNAPSHOT.jar /app.jar
COPY start.sh /start.sh

ENTRYPOINT ["/bin/sh", "/start.sh"]