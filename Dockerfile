FROM openjdk:11

WORKDIR /srv

RUN apt update && apt -y install maven

COPY . .

RUN mvn clean compile

EXPOSE 8080

CMD mvn exec:java
