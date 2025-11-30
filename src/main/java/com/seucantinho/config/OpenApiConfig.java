package com.seucantinho.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração do OpenAPI (Swagger) para documentação da API.
 * Adiciona informações básicas da API e configura o esquema de segurança (JWT Bearer).
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Seu Cantinho API - Aluguel de Espaços",
                version = "v1.0",
                description = "API RESTful para gerenciamento de filiais, espaços e reservas.",
                contact = @Contact(
                        name = "Equipe de Desenvolvimento",
                        email = "contato@seucantinho.com"
                ),
                license = @License(
                        name = "Licença XYZ",
                        url = "https://www.example.com"
                )
        )
)
public class OpenApiConfig {

    private static final String SCHEME_NAME = "bearerAuth";
    private static final String SCHEME = "Bearer";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 1. Define o esquema de segurança (JWT/Bearer)
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME, createSecurityScheme()))
                // 2. Aplica o esquema de segurança globalmente (todas as rotas)
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(SCHEME)
                .bearerFormat("JWT")
                .description("Insira o token JWT com o prefixo 'Bearer '.");
    }
}
