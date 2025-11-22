package com.seucantinho.controller;

import com.seucantinho.model.Administrador;
import com.seucantinho.service.AdministradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administradores")
@Tag(name = "Administradores", description = "Gerenciamento de usuários administradores e suas filiais associadas.")
public class AdministradorController {

    private final AdministradorService administradorService;

    @Autowired
    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    // ------------------------------------------------------------------------
    // CRUD E REGISTRO
    // ------------------------------------------------------------------------

    @Operation(summary = "Registra um novo Administrador",
               description = "Endpoint para cadastrar um novo usuário com privilégios de administrador.")
    @PostMapping("/registrar")
    public ResponseEntity<Administrador> registrarAdministrador(@Valid @RequestBody Administrador administrador) {
        // O Service cuida da validação de e-mail único e da codificação da senha.
        Administrador novoAdmin = administradorService.registrarAdministrador(administrador);
        // Retorna 201 Created
        return new ResponseEntity<>(novoAdmin, HttpStatus.CREATED);
    }

    @Operation(summary = "Lista todos os Administradores",
               description = "Retorna uma lista de todos os administradores cadastrados.")
    @GetMapping
    public ResponseEntity<List<Administrador>> listarTodos() {
        // Esta rota deve ser restrita apenas a Super-Administradores ou auditores.
        List<Administrador> administradores = administradorService.listarTodos();
        return ResponseEntity.ok(administradores);
    }

    @Operation(summary = "Busca um Administrador pelo ID",
               description = "Retorna os detalhes de um administrador específico.")
    @GetMapping("/{id}")
    public ResponseEntity<Administrador> buscarPorId(@PathVariable String id) {
        Administrador administrador = administradorService.buscarPorId(id);
        return ResponseEntity.ok(administrador);
    }

    // ------------------------------------------------------------------------
    // GESTÃO DE FILIAIS
    // ------------------------------------------------------------------------

    @Operation(summary = "Adiciona Filial Gerenciada",
               description = "Associa uma Filial ao conjunto de filiais que este administrador supervisiona.")
    @PostMapping("/{idAdmin}/filiais/{idFilial}")
    public ResponseEntity<Administrador> adicionarFilial(
            @PathVariable String idAdmin,
            @PathVariable String idFilial) {

        Administrador administradorAtualizado = administradorService.adicionarFilialGerenciada(idAdmin, idFilial);
        return ResponseEntity.ok(administradorAtualizado);
    }

    @Operation(summary = "Remove Filial Gerenciada",
               description = "Remove uma Filial do conjunto de filiais supervisionadas por este administrador.")
    @DeleteMapping("/{idAdmin}/filiais/{idFilial}")
    public ResponseEntity<Administrador> removerFilial(
            @PathVariable String idAdmin,
            @PathVariable String idFilial) {

        Administrador administradorAtualizado = administradorService.removerFilialGerenciada(idAdmin, idFilial);
        return ResponseEntity.ok(administradorAtualizado);
    }
}