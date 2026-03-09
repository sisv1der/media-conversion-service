# syntax=docker/dockerfile:1.4
FROM --platform=$BUILDPLATFORM eclipse-temurin:25-jdk AS builder
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline

COPY src src
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests

RUN java -Djarmode=layertools -jar target/*.jar extract

FROM eclipse-temurin:25-jre

RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

WORKDIR /app
COPY --from=builder --chown=spring:spring /app/dependencies/ ./
COPY --from=builder --chown=spring:spring /app/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /app/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /app/application/ ./

EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]