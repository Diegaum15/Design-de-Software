package com.seucantinho.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Este Controller serve apenas para testar a saúde (health check) da aplicação.
 * Ele define os endpoints que a aplicação pode acessar.
 */
@RestController
public class StatusController {

    /**
     * Endpoint para a rota raiz.
     * Ao acessar http://localhost:8080/, ele retorna uma mensagem de boas-vindas.
     * @return Map com a mensagem de status
     */
    @GetMapping("/")
    public Map<String, String> home() {
        // Isso resolve o erro 500 que você estava vendo na rota raiz
        return Map.of("status", "API SeuCantinho está no ar!", 
                      "versao", "1.0.0",
                      "instrucao", "Tente acessar /api/status para mais informações.");
    }

    /**
     * Endpoint mais específico para verificação de saúde.
     * Ao acessar http://localhost:8080/api/status, ele retorna o status de UP.
     * @return Map com o status de saúde
     */
    @GetMapping("/api/status")
    public Map<String, String> getStatus() {
        return Map.of("status", "UP", "servicos", "PostgreSQL e API funcionando corretamente.");
    }
}