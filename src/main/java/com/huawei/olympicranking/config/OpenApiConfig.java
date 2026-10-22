package com.huawei.olympicranking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("2024 Paris Olympics Medal Ranking API")
                        .version("1.0.0")
                        .description("Medal tracking and ranking REST API for the 2024 Paris Olympics")
                        .contact(new Contact().name("Huawei Candidate")));
    }
}
