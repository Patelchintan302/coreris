package com.example.coreris.security;

import com.example.coreris.entity.User;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

//    static {
//        SpringDocUtils.getConfig().addRequestWrapperToIgnore(User.class);
//    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoreRIS API Documentation")
                        .version("1.0")
                        .description("API documentation and console testing for the Radiology Information System backend."))
                // Enable security globally for all Swagger test calls
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        // Define the Bearer JWT token header configuration
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}