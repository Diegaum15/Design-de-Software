package com.seucantinho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_RESERVA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idReserva;

    // Relacionamento N:1 com Cliente (Um Cliente pode ter muitas Reservas)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    // Relacionamento N:1 com Espaco (Um Espaco pode ter muitas Reservas)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_espaco", nullable = false)
    private Espaco espaco;

    @Column(name = "data_reserva")
    private LocalDateTime dataReserva = LocalDateTime.now();

    @Column(name = "data_evento", nullable = false)
    private LocalDateTime dataEvento;
    
    @Column(name = "valor_pago")
    private float valorPago;
    
    @Column(name = "status_reserva")
    private String statusReserva; // Ex: PENDENTE, CONFIRMADA, CANCELADA

    // Relacionamento 1:1 com Pagamento (Uma Reserva tem um Pagamento)
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Pagamento pagamento;
}