package com.example.myhouse24rest.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "admin",
                        email = "admin@gmail.com"
                ),
                description = "OpenAPI documentation for MyHouse24",
                title = "OpenAPI specification - CRM MyHouse24",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https://license.com"
                ),
                termsOfService = "Term of service"
        ),
        servers = {
                @Server(
                        description = "Server",
                        url = "/my-house/"),
                @Server(
                        description = "Local",
                        url = "http://localhost:8080/my-house")
        })
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}

