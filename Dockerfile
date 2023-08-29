FROM eclipse-temurin:17-alpine as builder

WORKDIR /app
COPY target/assets.jar ./app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:17-alpine
WORKDIR /opt/assets
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./
EXPOSE 8080

CMD ["java", "-Dfile.encoding=UTF-8", "-Djava.security.egd=file:/dev/urandom", "-Dspring.profiles.active=stage", "org.springframework.boot.loader.JarLauncher"]
