FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY broker/.mvn/ .mvn
COPY broker/mvnw broker/pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY broker/src ./src
RUN ./mvnw package -DskipTests -B
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN if ! getent group springuser > /dev/null; then groupadd --system springuser; fi && \
    if ! getent passwd springuser > /dev/null; then useradd --system --gid springuser --home-dir /home/springuser --create-home springuser; fi && \
    mkdir -p /home/springuser/saves && \
    chown -R springuser:springuser /home/springuser/saves
USER springuser
EXPOSE 8083
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8083/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
