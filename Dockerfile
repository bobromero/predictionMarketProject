FROM eclipse-temurin:25-jdk-alpine
LABEL authors="Robert"
ARG JAR_FILE=target/*.jar
COPY target/predictionMarket-*.jar app.jar
EXPOSE 8082

ENTRYPOINT ["java","-jar","app.jar"]