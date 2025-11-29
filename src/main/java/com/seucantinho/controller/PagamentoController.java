package com.seucantinho.controller;

import com.seucantinho.dto.AtualizarStatusPagamentoRequest;
import com.seucantinho.dto.PagamentoRequest;
import com.seucantinho.model.Pagamento;
import com.seucantinho.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos", description = "Processamento e gestão de pagamentos de reservas.")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @Autowired
    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    // ------------------------------
    // PROCESSAR PAGAMENTO (POST)
    // ------------------------------

    @Operation(summary = "Processa o pagamento de uma reserva")
    @PostMapping("/processar")
    public ResponseEntity<Pagamento> processarPagamento(
            @Valid @RequestBody PagamentoRequest request) {

        Pagamento pagamento = pagamentoService.processarPagamento(request);
        return ResponseEntity.ok(pagamento);
    }

    // ------------------------------
    // GET /{id}
    // ------------------------------

    @Operation(summary = "Busca um pagamento pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPagamento(@PathVariable String id) {
        Pagamento pagamento = pagamentoService.buscarPorId(id);
        return ResponseEntity.ok(pagamento);
    }

    // ------------------------------
    // PUT /{id}/status
    // ------------------------------

    @Operation(summary = "Atualiza o status de um pagamento")
    @PutMapping("/{id}/status")
    public ResponseEntity<Pagamento> atualizarStatus(
            @PathVariable String id,
            @Valid @RequestBody AtualizarStatusPagamentoRequest request) {

        Pagamento pagamentoAtualizado = pagamentoService.atualizarStatus(id, request.getStatus());
        return ResponseEntity.ok(pagamentoAtualizado);
    }

    // ------------------------------
    // DELETE /{id}
    // ------------------------------

    @Operation(summary = "Cancela ou estorna um pagamento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarPagamento(@PathVariable String id) {
        pagamentoService.cancelarPagamento(id);
        return ResponseEntity.noContent().build();
    }
}
