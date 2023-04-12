FROM eclipse-temurin:17

WORKDIR /srv

RUN apt update && apt -y install maven

COPY . .

ENV SPRING_DATASOURCE_URL jdbc:postgresql://mindcode-db/mindcode_development
ENV SPRING_DATASOURCE_USERNAME postgres
ENV SPRING_DATASOURCE_PASSWORD pg_password

# Skip tests because postgres is only available at runtime
RUN ./mvnw install -Dmaven.test.skip

EXPOSE 8080

CMD java --enable-preview -classpath `find webapp -type f -name '*.jar' | tr '\n' ':'` info.teksol.mindcode.webapp.WebappApplication
