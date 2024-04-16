FROM maven:3.8.5-openjdk-17-slim AS build
COPY my-house24-admin/src /home/app/src
COPY my-house24-admin/pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:17
COPY --from=build /home/app/target/*.jar MyHouse24_Lesyk_Rudiuk_Admin.jar
ENTRYPOINT ["java", "-jar", "MyHouse24_Lesyk_Rudiuk_Admin.jar"]