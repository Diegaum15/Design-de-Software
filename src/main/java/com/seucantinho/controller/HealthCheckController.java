package com.seucantinho.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Hidden // Oculta este endpoint no Swagger (pois é apenas para checagem interna/saúde)
public class HealthCheckController {

    /**
     * Endpoint simples para verificar se a API está de pé e conectada ao banco de dados.
     * Em um ambiente real, você faria uma consulta simples ao DB aqui.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> checkStatus() {
        
        // Simulação de resposta de saúde (Health Check)
        Map<String, String> response = new HashMap<>();
        response.put("servicos", "API REST em execução.");
        response.put("status", "UP");
        
        // Em um sistema real, você faria:
        // try {
        //     dbService.checkConnection();
        //     response.put("db", "CONNECTED");
        // } catch (Exception e) {
        //     response.put("db", "DOWN");
        // }
        
        return ResponseEntity.ok(response);
    }
}