@Entity
@Table(name = "TB_RESERVA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_espaco", nullable = false)
    private Espaco espaco;

    @Column(name = "data_reserva")
    private LocalDateTime dataReserva = LocalDateTime.now();

    @Column(name = "data_inicio_evento", nullable = false)
    private LocalDateTime dataInicioEvento;

    @Column(name = "data_fim_evento", nullable = false)
    private LocalDateTime dataFimEvento;

    private float valorPago;

    @Column(name = "status_reserva")
    private String statusReserva; // PENDENTE, CONFIRMADA, CANCELADA

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Pagamento pagamento;
}
