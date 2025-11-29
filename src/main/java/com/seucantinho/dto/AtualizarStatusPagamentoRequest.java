package com.seucantinho.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AtualizarStatusPagamentoRequest {

    @NotBlank(message = "O novo status é obrigatório.")
    private String status; // DEVOLVIDO, ESTORNADO, CANCELADO, etc
}
