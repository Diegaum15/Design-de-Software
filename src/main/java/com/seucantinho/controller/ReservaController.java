@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<Reserva> criar(@RequestBody Reserva reserva) {
        return ResponseEntity.ok(reservaService.criarReserva(reserva));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscar(@PathVariable String id) {
        return ResponseEntity.of(reservaService.buscar(id));
    }
}
