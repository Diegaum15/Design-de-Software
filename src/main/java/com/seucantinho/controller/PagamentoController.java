package com.seucantinho.controller;

import com.seucantinho.dto.PagamentoRequest;
import com.seucantinho.model.Pagamento;
import com.seucantinho.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos", description = "Gerenciamento e processamento de pagamentos de reservas.")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @Autowired
    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    // ------------------------------------------------------------------------
    // PROCESSAMENTO (PÚBLICO / Cliente)
    // ------------------------------------------------------------------------

    @Operation(summary = "Processa o pagamento de uma reserva",
               description = "Recebe os dados de pagamento (simulados) para uma reserva PENDENTE. Se o processamento for bem-sucedido, a reserva é CONFIRMADA.")
    @PostMapping("/processar")
    public ResponseEntity<Pagamento> processarPagamento(@Valid @RequestBody PagamentoRequest request) {
        
        // O ID da reserva está dentro do PagamentoRequest.
        String idReserva = request.getIdReserva();
        
        // O PagamentoService lida com a simulação do gateway, salva o registro
        // de pagamento e chama o ReservaService para confirmar a reserva.
        Pagamento pagamentoSalvo = pagamentoService.processarPagamento(idReserva, request);
        
        // Retorna 201 Created para indicar que a transação e a confirmação foram bem-sucedidas.
        return new ResponseEntity<>(pagamentoSalvo, HttpStatus.CREATED);
    }
    
    // ------------------------------------------------------------------------
    // CONSULTA (Privado / Cliente & Admin)
    // ------------------------------------------------------------------------

    @Operation(summary = "Busca um Pagamento pelo ID",
               description = "Retorna os detalhes de um registro de pagamento específico.")
    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable String id) {
        Pagamento pagamento = pagamentoService.buscarPorId(id);
        return ResponseEntity.ok(pagamento);
    }
}