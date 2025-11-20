package com.seucantinho.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Indica que esta classe fornece beans de configuração
public class SwaggerConfig {

    @Bean // Define um bean que o Spring irá gerenciar
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Seu Cantinho - Gerenciamento de Reservas") // Título da API
                .version("1.0.0") // Versão da API
                .description("API RESTful para gerenciamento de reservas de espaços (Salões, Chácaras, Quadras Esportivas) para o projeto Seu Cantinho.")
                .contact(new Contact()
                    .name("Diego - Equipe DesignSoft")
                    .email("contato@seucantinho.com.br")
                    .url("http://www.seucantinho.com.br")
                )
                .license(new License()
                    .name("Licença de Uso")
                    .url("http://www.seucantinho.com.br/license")
                )
            );
    }

    // Se no futuro você implementar Spring Security,
    // a configuração de JWT (Bearer Token) para autenticação
    // deve ser adicionada neste bean OpenAPI.
}
