package com.seucantinho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PAGAMENTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idPagamento;

    // Relacionamento 1:1 com Reserva (Um Pagamento pertence a uma Reserva)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;
    
    private float valor;
    
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;
    
    private String status; // Ex: SINAL, QUITADO, FALHOU
}