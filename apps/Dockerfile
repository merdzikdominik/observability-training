ARG MODULE
ARG OTEL_AGENT_VERSION=2.31.1

FROM maven:3.9.11-eclipse-temurin-21 AS builder
ARG MODULE
WORKDIR /src
COPY pom.xml ./
COPY apps/orders-api/pom.xml apps/orders-api/pom.xml
COPY apps/inventory-api/pom.xml apps/inventory-api/pom.xml
COPY apps/payment-api/pom.xml apps/payment-api/pom.xml
RUN mvn -q -pl apps/${MODULE} -am dependency:go-offline
COPY apps ./apps
RUN mvn -q -pl apps/${MODULE} -am package -DskipTests

FROM eclipse-temurin:21-jre
ARG MODULE
ARG OTEL_AGENT_VERSION
RUN useradd --system --uid 10001 app && mkdir -p /app /otel /var/log/training && chown -R app:app /app /otel /var/log/training
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar
RUN chmod 0644 /otel/opentelemetry-javaagent.jar
COPY --from=builder /src/apps/${MODULE}/target/*.jar /app/app.jar
USER app
ENTRYPOINT ["java","-jar","/app/app.jar"]
