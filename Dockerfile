FROM openjdk:15
WORKDIR /opt/edgeserver
COPY . /opt/edgeserver
EXPOSE 8080
ARG env=abc
ENV env_arg=${env}
RUN chmod +x mvnw
RUN ./mvnw clean package
ENTRYPOINT ["java", "-Dspring.profiles.active=${env_arg}", "-jar", "-Xms700M", " -Xmx700M", "target/app.jar"]
