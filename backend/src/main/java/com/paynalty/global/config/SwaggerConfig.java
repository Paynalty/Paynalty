package com.paynalty.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "JWT";

        // JWT 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        // JWT SecurityScheme 설정
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT 토큰을 입력하세요 (Bearer 제외)")
                );

        return new OpenAPI()
                .info(new Info()
                        .title("Paynalty API")
                        .description("Paynalty 백엔드 API 문서")
                        .version("v1.0.0")
                )
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}