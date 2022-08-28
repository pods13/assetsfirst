FROM eclipse-temurin:17-alpine

WORKDIR /opt/assets
COPY target/assets.jar ./app.jar
EXPOSE 8080

CMD ["java", "-Dfile.encoding=UTF-8", "-Djava.security.egd=file:/dev/urandom", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
