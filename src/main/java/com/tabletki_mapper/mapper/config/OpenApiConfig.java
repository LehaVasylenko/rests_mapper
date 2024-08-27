package com.tabletki_mapper.mapper.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mapper
 * Author: Vasylenko Oleksii
 * Date: 06.08.2024
 */
@Slf4j
@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/Import/**", "/help/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Маппер Tabletki.ua API до Geoapteka API")
                        .version("1.0")
                        .description("Дозволяє використовувати API Tabletki.ua для передачі залишків із ТТ до ресурсу Geoapteka")
                        .contact(new Contact().name("Василенко Олексій +380504302231").email("contact@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

}
