FROM openjdk:15
WORKDIR /opt/edgeserver
COPY . /opt/edgeserver
EXPOSE 8080
RUN chmod +x mvnw
RUN ./mvnw clean package
ENTRYPOINT ["java" ,"-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=/dump", "-jar", "target/app.jar"]
#, "-Xmx600M"