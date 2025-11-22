package com.seucantinho.controller;

import com.seucantinho.dto.ReservaRequest;
import com.seucantinho.model.Reserva;
import com.seucantinho.service.ReservaService;
import com.seucantinho.model.Cliente;
import com.seucantinho.model.Espaco;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Gerenciamento e criação de reservas de espaços.")
public class ReservaController {

    private final ReservaService reservaService;

    @Autowired
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // ------------------------------------------------------------------------
    // CRIAÇÃO (PÚBLICO / Cliente)
    // ------------------------------------------------------------------------

    @Operation(summary = "Cria uma nova Reserva",
               description = "Inicia o processo de reserva. A reserva é criada com status PENDENTE e precisa de pagamento para ser CONFIRMADA.")
    
    @PostMapping
    public ResponseEntity<Reserva> criarReserva(@Valid @RequestBody ReservaRequest req) {

        Reserva reserva = new Reserva();

        Cliente cli = new Cliente();
        cli.setIdUsuario(req.getIdCliente());
        reserva.setCliente(cli);

        Espaco esp = new Espaco() {};
        esp.setIdEspaco(req.getIdEspaco());
        reserva.setEspaco(esp);

        reserva.setDataInicioEvento(req.getDataInicio());
        reserva.setValorPago(req.getValorTotal());

        Reserva criada = reservaService.criarReserva(reserva);

        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    // ------------------------------------------------------------------------
    // CONSULTA (Privado / Cliente & Admin)
    // ------------------------------------------------------------------------

    @Operation(summary = "Busca uma Reserva pelo ID",
               description = "Retorna os detalhes de uma reserva específica.")
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarPorId(@PathVariable String id) {
        Reserva reserva = reservaService.buscarPorId(id);
        return ResponseEntity.ok(reserva);
    }

    @Operation(summary = "Lista todas as Reservas de um Cliente",
               description = "Retorna uma lista de reservas feitas por um cliente específico.")
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Reserva>> listarReservasPorCliente(@PathVariable String idCliente) {
        List<Reserva> reservas = reservaService.listarReservasPorCliente(idCliente);
        return ResponseEntity.ok(reservas);
    }
    
    @Operation(summary = "Lista todas as Reservas do Sistema",
               description = "Retorna todas as reservas, ideal para painel administrativo. (Acesso restrito a Administradores).")
    @GetMapping
    public ResponseEntity<List<Reserva>> listarTodas() {
        // Nota: O ReservaService deve ser expandido para incluir listarTodos() se for necessário.
        // Assumindo que este método exista:
        // List<Reserva> reservas = reservaService.listarTodas();
        // return ResponseEntity.ok(reservas);
        
        // Para fins deste exemplo, usaremos uma implementação temporária ou expandimos o service.
        // Vamos supor que apenas as rotas de cliente/ID são necessárias por enquanto.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}