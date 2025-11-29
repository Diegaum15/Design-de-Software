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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos", description = "Processamento e gestão de pagamentos de reservas.")
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
        Pagamento pagamento = pagamentoService.processarPagamento(request);
        
        // Se o status for "QUITADO", retorna 200 OK ou 201 Created
        if (pagamento.getStatus().equals("QUITADO")) {
            return new ResponseEntity<>(pagamento, HttpStatus.CREATED);
        }
        
        // Se falhou, retorna 400 Bad Request ou 402 Payment Required (depende da arquitetura)
        return new ResponseEntity<>(pagamento, HttpStatus.BAD_REQUEST);
    }
}