# build
FROM maven:3.6.3-jdk-8-slim as builder
COPY . /app
WORKDIR /app
RUN mvn -B clean package -DskipTests

# run
FROM openjdk:11-jre

ARG JAR_FILE=target/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar