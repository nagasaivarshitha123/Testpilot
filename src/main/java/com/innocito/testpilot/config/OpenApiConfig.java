package com.innocito.testpilot.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Map;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Test Pilot API", version = "1.0",
        description = "Using Test Pilot can create test cases and test suits for your REST APIs"),
        security = {@SecurityRequirement(name = "Bearer Token")}
)
@SecuritySchemes({
        @SecurityScheme(name = "Bearer Token", type = SecuritySchemeType.HTTP,
                scheme = "bearer", bearerFormat = "JWT")
})
public class OpenApiConfig {

    @Value("${app.version}")
    private String projectVersion;
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.getInfo().setVersion(projectVersion);
            openApi.getInfo().setExtensions(Map.of("release_timestamp", new Date(),
                    "activeProfile", activeProfile));
        };
    }
}