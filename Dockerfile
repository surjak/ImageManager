FROM openjdk:15
WORKDIR /opt/edgeserver
COPY . /opt/edgeserver
EXPOSE 8080
ARG env=dev
RUN chmod +x mvnw
RUN ./mvnw clean package
ENTRYPOINT ["java", "-Dspring.profiles.active=$env", "-jar", "target/app.jar"]
