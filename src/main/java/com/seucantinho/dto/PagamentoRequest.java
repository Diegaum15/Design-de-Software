package com.seucantinho.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO para receber os dados necessários para processar um pagamento.
 * Simula os dados de um cartão de crédito.
 */
@Data
public class PagamentoRequest {

    @NotBlank(message = "O ID da reserva é obrigatório.")
    private String idReserva;

    @NotBlank(message = "O número do cartão é obrigatório.")
    // Usamos um Pattern simplificado para simulação (16 dígitos)
    @Pattern(regexp = "^\\d{16}$", message = "Número do cartão deve ter 16 dígitos.")
    private String numeroCartao;

    @NotBlank(message = "O nome no cartão é obrigatório.")
    private String nomeNoCartao;

    // Outros campos simulados para pagamento (Validade, CVV, etc.) poderiam ser adicionados aqui.
}