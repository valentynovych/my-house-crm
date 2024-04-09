FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-alpine
WORKDIR /app

COPY --from=build /app/target/MyHouse24_Lesyk_Rudiuk_Admin.jar .
ENTRYPOINT ["java","-jar","MyHouse24_Lesyk_Rudiuk_Admin.jar"]