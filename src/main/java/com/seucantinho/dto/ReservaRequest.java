package com.seucantinho.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaRequest {

    @NotBlank(message = "O ID do cliente é obrigatório.")
    private String idCliente;

    @NotBlank(message = "O ID do espaço é obrigatório.")
    private String idEspaco;

    @NotNull(message = "A data e hora de início do evento são obrigatórias.")
    private LocalDateTime dataEventoInicio;
    
    // Opcional, se você quiser que o cliente defina a duração
    private LocalDateTime dataEventoFim; 

    @NotNull(message = "O valor total da reserva é obrigatório.")
    private Float valorTotal;
}