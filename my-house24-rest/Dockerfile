FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY my-house24-rest/pom.xml .
COPY my-house24-rest/src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-alpine
WORKDIR /app

COPY --from=build /app/target/MyHouse24_Lesyk_Rudiuk_Rest.jar .
ENTRYPOINT ["java","-jar","MyHouse24_Lesyk_Rudiuk_Rest.jar"]