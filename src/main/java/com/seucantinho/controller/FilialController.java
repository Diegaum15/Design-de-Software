package com.seucantinho.controller;

import com.seucantinho.model.Filial;
import com.seucantinho.service.FilialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/filiais") // URL base para todos os endpoints deste controller
@Tag(name = "Filiais", description = "Gerenciamento de unidades e locais (Filiais) do Seu Cantinho.")
public class FilialController {

    private final FilialService filialService;

    @Autowired
    public FilialController(FilialService filialService) {
        this.filialService = filialService;
    }

    /**
     * Endpoint para criar uma nova filial.
     */
    @Operation(summary = "Cria uma nova filial",
               description = "Registra uma nova unidade de negócio no sistema. Requer privilégios de Administrador.")
    @PostMapping
    public ResponseEntity<Filial> criarFilial(@Valid @RequestBody Filial filial) {
        Filial novaFilial = filialService.salvarFilial(filial);
        // Retorna 201 Created
        return new ResponseEntity<>(novaFilial, HttpStatus.CREATED);
    }

    /**
     * Endpoint para listar todas as filiais.
     */
    @Operation(summary = "Lista todas as filiais",
               description = "Retorna uma lista de todas as filiais cadastradas.")
    @GetMapping
    public ResponseEntity<List<Filial>> listarTodas() {
        List<Filial> filiais = filialService.listarTodas();
        return ResponseEntity.ok(filiais);
    }

    /**
     * Endpoint para buscar uma filial pelo ID.
     */
    @Operation(summary = "Busca uma filial pelo ID",
               description = "Retorna os detalhes de uma filial específica usando seu ID. Retorna 404 se não encontrada.")
    @GetMapping("/{id}")
    public ResponseEntity<Filial> buscarPorId(@PathVariable String id) {
        // O service lança EntityNotFoundException (que é tratada pelo GlobalExceptionHandler)
        Filial filial = filialService.buscarPorId(id);
        return ResponseEntity.ok(filial);
    }

    /**
     * Endpoint para atualizar uma filial existente.
     */
    @Operation(summary = "Atualiza uma filial existente",
               description = "Atualiza nome, endereço, telefone ou status de uma filial existente.")
    @PutMapping("/{id}")
    public ResponseEntity<Filial> atualizarFilial(@PathVariable String id, @Valid @RequestBody Filial filialDetalhes) {
        // Garante que o ID da URL seja usado para a atualização
        filialDetalhes.setIdFilial(id); 
        
        // Embora usemos salvarFilial, em um sistema real, faríamos um PUT/PATCH mais específico
        Filial filialAtualizada = filialService.salvarFilial(filialDetalhes); 
        return ResponseEntity.ok(filialAtualizada);
    }

    /**
     * Endpoint para desativar (mudar status para inativo) uma filial.
     */
    @Operation(summary = "Desativa uma filial",
               description = "Define o status da filial como 'inativo' (exclusão lógica).")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarFilial(@PathVariable String id) {
        filialService.desativarFilial(id);
        // Retorna 204 No Content para indicar sucesso na operação sem corpo de resposta
        return ResponseEntity.noContent().build(); 
    }
}