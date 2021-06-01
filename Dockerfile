FROM openjdk:11
WORKDIR /opt/edgeserver
COPY . /opt/edgeserver
EXPOSE 8080
RUN chmod +x mvnw
RUN ./mvnw clean package
ENTRYPOINT exec java $JAVA_OPTS -jar target/app.jar
#, "-Xmx600M"