FROM openjdk:15
WORKDIR /opt/edgeserver
COPY . /opt/edgeserver
EXPOSE 8080
ARG env=abc
ENV env_arg=${env}
RUN chmod +x mvnw
RUN ./mvnw clean package
ENTRYPOINT ["java", "-Xmx700M" , "-Dspring.profiles.active=${env_arg}", "-jar", "target/app.jar"]
