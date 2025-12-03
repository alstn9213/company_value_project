package com.back.config;

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
        return new OpenAPI()
                .info(new Info()
                        .title("Value Pick API Document")
                        .description("기업 가치 평가 및 거시 경제 분석 플랫폼 API 명세서")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Auth")) // JWT 인증 설정
                .components(new Components()
                        .addSecuritySchemes("Bearer Auth", new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
