package com.picko.api.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Picko API")
                        .description("K-SPOT — 한국 트렌드 장소 탐색·공유 서비스 백엔드 API")
                        .version("v1.0.0"));
    }
}
