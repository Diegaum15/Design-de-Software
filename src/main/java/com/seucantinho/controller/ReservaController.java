package com.seucantinho.controller;

import com.seucantinho.dto.ReservaRequest;
import com.seucantinho.model.Reserva;
import com.seucantinho.service.ReservaService;
import com.seucantinho.dto.ReservaResponse;
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
    public ResponseEntity<Reserva> criarReserva(@Valid @RequestBody ReservaRequest request) {
        
        // 1. Mapeamento do DTO de Request para a Entidade Reserva
        Reserva novaReserva = new Reserva();
        
        // Define o cliente (apenas o ID é necessário para o Service buscar)
        Cliente cliente = new Cliente();
        cliente.setIdUsuario(request.getIdCliente());
        novaReserva.setCliente(cliente);

        // Define o espaço (apenas o ID é necessário para o Service buscar)
        Espaco espaco = new Espaco() {}; // Instanciação da classe abstrata
        espaco.setIdEspaco(request.getIdEspaco());
        novaReserva.setEspaco(espaco);

        // Define as datas e o valor
        novaReserva.setDataEvento(request.getDataEventoInicio()); 
        
        novaReserva.setValorPago(request.getValorTotal()); // Valor total da reserva

        // 2. Chama o Service para validação de disponibilidade e salvamento
        Reserva reservaSalva = reservaService.criarReserva(novaReserva);
        
        // Retorna a reserva criada (status PENDENTE)
        return new ResponseEntity<>(reservaSalva, HttpStatus.CREATED);
    }

    // ------------------------------------------------------------------------
    // CONSULTA (Privado / Cliente & Admin)
    // ------------------------------------------------------------------------

    @Operation(summary = "Busca uma Reserva pelo ID",
           description = "Retorna os detalhes de uma reserva específica.")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> buscarPorId(@PathVariable String id) {
        ReservaResponse response = reservaService.buscarPorIdDTO(id); // Chamada ajustada
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista todas as Reservas de um Cliente",
            description = "Retorna uma lista de reservas feitas por um cliente específico.")
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<ReservaResponse>> listarReservasPorCliente(@PathVariable String idCliente) {
        List<ReservaResponse> reservas = reservaService.listarReservasPorCliente(idCliente); 
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Lista todas as Reservas do Sistema",
            description = "Retorna todas as reservas, ideal para painel administrativo.")
    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listarTodas() {
        List<ReservaResponse> reservas = reservaService.listarTodas();
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Atualiza uma Reserva",
           description = "Permite alterar data, espaço, cliente ou valor.")
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> atualizarReserva(
            @PathVariable String id,
            @Valid @RequestBody ReservaRequest request) {

        Reserva reservaAtualizada = new Reserva();

        // Cliente
        Cliente cliente = new Cliente();
        cliente.setIdUsuario(request.getIdCliente());
        reservaAtualizada.setCliente(cliente);

        // Espaço
        Espaco espaco = new Espaco() {};
        espaco.setIdEspaco(request.getIdEspaco());
        reservaAtualizada.setEspaco(espaco);

        // Datas
        reservaAtualizada.setDataEvento(request.getDataEventoInicio());
        reservaAtualizada.setDataEventoFim(request.getDataEventoFim());

        // Valor
        reservaAtualizada.setValorPago(request.getValorTotal());

        Reserva reservaSalva = reservaService.atualizarReserva(id, reservaAtualizada);

        return ResponseEntity.ok(reservaService.toResponse(reservaSalva));
    }

    @Operation(summary = "Deleta uma Reserva pelo ID",
           description = "Remove uma reserva permanentemente.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarReserva(@PathVariable String id) {
        reservaService.deletarReserva(id);
        return ResponseEntity.noContent().build();
    }

}