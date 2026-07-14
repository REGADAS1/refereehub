package com.regadas.refereehub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI refereeHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RefereeHub API")
                        .version("0.1.0")
                        .description("API for managing referee matches, payments, financial summaries and dashboard statistics."));
    }
}