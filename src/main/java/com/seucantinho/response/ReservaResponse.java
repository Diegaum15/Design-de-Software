package com.seucantinho.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReservaResponse {
    private String idReserva;
    private String idCliente;
    private String nomeCliente; 
    private String idEspaco;
    private String nomeEspaco; 
    private LocalDateTime dataReserva;
    private LocalDateTime dataEvento;
    private Float valorPago;
    private String statusReserva;
    private String statusPagamento; 
}