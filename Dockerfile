FROM eclipse-temurin:22

WORKDIR /srv

RUN apt update && apt -y install maven

COPY pom.xml pom.xml
COPY annotations/pom.xml annotations/pom.xml
COPY compiler/pom.xml compiler/pom.xml
COPY exttest/pom.xml exttest/pom.xml
COPY samples/pom.xml samples/pom.xml
COPY schemacode/pom.xml schemacode/pom.xml
COPY toolapp/pom.xml toolapp/pom.xml
COPY webapp/pom.xml webapp/pom.xml

RUN mvn dependency:go-offline

COPY . .

ENV SPRING_DATASOURCE_URL jdbc:postgresql://mindcode-db:5432/mindcode_development
ENV SPRING_DATASOURCE_USERNAME postgres
ENV SPRING_DATASOURCE_PASSWORD pg_password

# Skip tests because postgres is only available at runtime
RUN mvn clean package -Dmaven.test.skip

EXPOSE 8080

CMD java -jar webapp/target/mindcode-webapp.jar
