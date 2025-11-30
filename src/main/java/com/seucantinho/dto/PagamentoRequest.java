package com.seucantinho.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

@Data 
public class PagamentoRequest {

    // ID da reserva que este pagamento se destina
    @NotBlank(message = "O ID da reserva a ser paga é obrigatório.")
    private String idReserva;

    // Tipo de pagamento: CARTAO_CREDITO, PIX, BOLETO
    @NotBlank(message = "O método de pagamento é obrigatório.")
    private String metodoPagamento;

    // Campo simulado para o número do cartão (para validação básica)
    @CreditCardNumber(ignoreNonDigitCharacters = true, message = "Número de cartão inválido.")
    @NotBlank(message = "O número do cartão é obrigatório.")
    private String numeroCartao;
    
    @NotBlank(message = "O nome impresso no cartão é obrigatório.")
    private String nomeTitular;

    @NotNull(message = "O código CVV é obrigatório.")
    private Integer cvv;

    // Valor que o cliente está tentando pagar (para verificar se coincide com o valor da reserva)
    @NotNull(message = "O valor do pagamento é obrigatório.")
    private Float valorPagamento;
}