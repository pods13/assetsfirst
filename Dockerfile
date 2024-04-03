FROM eclipse-temurin:21-jre-alpine as builder

WORKDIR /app
COPY target/assets.jar ./app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/assets
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./
EXPOSE 8080

CMD ["java", "-XX:MaxRAMPercentage=95", "-Dfile.encoding=UTF-8", "-Djava.security.egd=file:/dev/urandom", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.JarLauncher"]
