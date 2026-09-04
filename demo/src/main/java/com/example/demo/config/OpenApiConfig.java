package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI applicationOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API")
                        .description(
                                "REST APIs for managing customers, " +
                                        "drivers, cultivation jobs and payments."
                        )
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name(applicationName))
                        .license(new License()
                                .name("Apache 2.0")));
    }
}