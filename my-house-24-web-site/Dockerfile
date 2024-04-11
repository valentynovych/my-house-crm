FROM maven:3.8.5-openjdk-17-slim AS build
COPY my-house-24-web-site/src /home/app/src
COPY my-house-24-web-site/pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM openjdk:17
COPY --from=build /home/app/target/*.jar MyHouse24_Lesyk_Rudiuk_WebSite.jar
ENTRYPOINT ["java", "-jar", "MyHouse24_Lesyk_Rudiuk_WebSite.jar"]
