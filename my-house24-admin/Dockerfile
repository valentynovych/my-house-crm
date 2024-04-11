FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

COPY my-house24-admin/pom.xml .
COPY my-house24-admin/src ./src

RUN mvn clean package -DskipTests

WORKDIR /app

FROM openjdk:17
COPY from=build /app/target/MyHouse24_Lesyk_Rudiuk_Admin.jar ./
# Set the command to run the application
CMD ["java", "-jar", "MyHouse24_Lesyk_Rudiuk_Admin.jar"]
#ADD my-house24-admin/target/MyHouse24_Lesyk_Rudiuk_Admin.jar .
#ENTRYPOINT ["java", "-jar", "MyHouse24_Lesyk_Rudiuk_Admin.jar"]