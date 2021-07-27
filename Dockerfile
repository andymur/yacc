# command to run it:
# docker run -e server.port=8080 -e bidders="http://localhost:8081/,http://localhost:8082/,http://localhost:8083" --network="host" bidding-platform-service:latest
FROM openjdk:8-jdk-alpine
MAINTAINER andymur.com
COPY target/bidding-platform-service.jar bidding-platform-service.jar
ENTRYPOINT ["java","-jar","/bidding-platform-service.jar"]
