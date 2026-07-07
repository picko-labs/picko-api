package com.picko.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Picko API")
                        .description("K-SPOT — 한국 트렌드 장소 탐색·공유 서비스 백엔드 API")
                        .version("v1.0.0"))
                .tags(List.of(
                        new Tag().name("연동용 | Auth").description("클라이언트 연동 · 인증"),
                        new Tag().name("연동용 | Spot").description("클라이언트 연동 · 스팟"),
                        new Tag().name("연동용 | Spot Categories").description("클라이언트 연동 · 스팟 카테고리"),
                        new Tag().name("연동용 | User").description("클라이언트 연동 · 사용자"),
                        new Tag().name("연동용 | Pin").description("클라이언트 연동 · 핀"),
                        new Tag().name("미연동용 | Spot").description("내부/어드민 · 스팟 관리"),
                        new Tag().name("미연동용 | Spot Categories").description("내부/어드민 · 스팟 카테고리 관리"),
                        new Tag().name("미연동용 | Spot Addresses").description("내부/어드민 · 스팟 주소 관리"),
                        new Tag().name("미연동용 | Pin").description("내부/어드민 · 핀 관리")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
