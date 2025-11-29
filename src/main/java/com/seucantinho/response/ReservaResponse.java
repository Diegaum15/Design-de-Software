package com.seucantinho.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservaResponse {
    private String idReserva;
    private String idCliente;
    private String nomeCliente; // Expor dados relevantes
    private String idEspaco;
    private String nomeEspaco; // Expor dados relevantes
    private LocalDateTime dataReserva;
    private LocalDateTime dataEvento;
    private Float valorPago;
    private String statusReserva;
    private String statusPagamento; // Status do pagamento associado
}