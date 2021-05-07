FROM openjdk:15
WORKDIR /opt/edgeserver
COPY . /opt/edgeserver
EXPOSE 8080
RUN chmod +x mvnw
RUN ./mvnw clean package
ENTRYPOINT ["java", "-Xmx400M" ,"-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=/dump", "-jar", "target/app.jar"]
