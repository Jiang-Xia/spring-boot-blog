package com.jiangxia.blog.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("江夏博客系统 API 文档")
                        .description("基于 Spring Boot 3.2 + JWT + JPA 的博客系统后端接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("江夏")
                                .email("jiang_xia_top@163.com")
                                .url("https://github.com/yourusername/spring-boot-blog"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("请输入 JWT Token")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
