FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY target/assets.jar ./app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /opt/assets
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD wget --spider http://localhost:8080/actuator/health || exit 1

CMD ["java", "-XX:MaxRAMPercentage=95", "-Dfile.encoding=UTF-8", "-Djava.security.egd=file:/dev/urandom", "-Dspring.profiles.active=prod", "org.springframework.boot.loader.launch.JarLauncher"]
