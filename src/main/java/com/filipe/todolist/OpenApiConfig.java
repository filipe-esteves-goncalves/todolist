package com.filipe.todolist;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Create the OpenAPI metadata used by Swagger UI and the OpenAPI
     * specification endpoint.
     *
     * @return configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI baggageBeltOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("TODO list Management API")
                        .description("""
                                REST API for managing TODO LIST
                                """)
                        .version("v1")
                        .contact(new Contact()
                                .name("IAG Technical Test")
                        ));

    }
}
