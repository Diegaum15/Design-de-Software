package com.seucantinho.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data // Gera Getters, Setters, ToString, EqualsAndHashCode
public class ReservaRequest {

    // ID do Cliente que está fazendo a reserva
    @NotBlank(message = "O ID do cliente é obrigatório para a reserva.")
    private String idCliente;

    // ID do Espaço que está sendo reservado
    @NotBlank(message = "O ID do espaço é obrigatório.")
    private String idEspaco;

    // Data e hora de início do evento. Usaremos LocalDateTime para precisão.
    @NotNull(message = "A data e hora de início do evento são obrigatórias.")
    private LocalDateTime dataEventoInicio;
    
    // Data e hora de fim do evento.
    // É importante para calcular a duração e verificar conflitos.
    @NotNull(message = "A data e hora de fim do evento são obrigatórias.")
    private LocalDateTime dataEventoFim;

    // Valor inicial, caso o cliente já saiba o valor total
    @NotNull(message = "O valor total da reserva deve ser especificado.")
    private Float valorTotal;
}