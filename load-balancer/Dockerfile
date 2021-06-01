FROM openjdk:15
WORKDIR /opt/edgeserver/load-balancer
COPY . /opt/edgeserver/load-balancer
EXPOSE 8080
RUN chmod +x mvnw
RUN ./mvnw clean package
ENTRYPOINT exec java $JAVA_OPTS -jar target/app.jar