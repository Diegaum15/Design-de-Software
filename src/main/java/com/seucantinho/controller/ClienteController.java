package com.seucantinho.controller;

import com.seucantinho.model.Cliente;
import com.seucantinho.service.ClienteService;
import com.seucantinho.dto.ClienteDTO; 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Gerenciamento de usuários clientes (registro, busca, atualização e exclusão).")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ------------------------------------------------------------------------
    // REGISTRO (PÚBLICO) - CREATE
    // ------------------------------------------------------------------------

    @Operation(summary = "Registra um novo Cliente",
               description = "Endpoint público para que novos usuários se cadastrem no sistema. Requer CPF e Email únicos.")
    @PostMapping("/registrar")
    public ResponseEntity<ClienteDTO> registrarCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO novoCliente = clienteService.registrarCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente); // 201 Created
    }

    // ------------------------------------------------------------------------
    // CONSULTA E ATUALIZAÇÃO - READ / UPDATE
    // ------------------------------------------------------------------------
    
    @Operation(summary = "Busca um Cliente pelo ID",
               description = "Retorna os dados de um cliente específico.")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable String id) {
        ClienteDTO cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente); // 200 OK
    }


    @Operation(summary = "Atualiza os dados de um Cliente",
               description = "Permite que um cliente atualize seu nome, telefone ou endereço.")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizarCliente(@PathVariable String id, @Valid @RequestBody ClienteDTO clienteDetalhesDTO) {
        ClienteDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteDetalhesDTO);
        return ResponseEntity.ok(clienteAtualizado); // 200 OK
    }
    
    // ------------------------------------------------------------------------
    // CONSULTAS ADMINISTRATIVAS - READ
    // ------------------------------------------------------------------------
    
    @Operation(summary = "Lista todos os Clientes",
               description = "Retorna uma lista de todos os clientes cadastrados. (Acesso restrito a Administradores).")
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarTodos() {
        List<ClienteDTO> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes); // 200 OK
    }
        
    @Operation(summary = "Busca um Cliente pelo CPF",
               description = "Busca um cliente utilizando o CPF. (Acesso restrito a Administradores).")
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteDTO> buscarPorCpf(@PathVariable String cpf) {
        ClienteDTO cliente = clienteService.buscarPorCpf(cpf);
        return ResponseEntity.ok(cliente); // 200 OK
    }
    

    // ------------------------------------------------------------------------
    // EXCLUSÃO - DELETE
    // ------------------------------------------------------------------------

    @Operation(summary = "Deleta um Cliente pelo ID",
            description = "Exclui permanentemente um cliente e seus dados. (Acesso restrito).")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable String id) {
        clienteService.deletarCliente(id);
        // Retorna 204 No Content para uma exclusão bem-sucedida
        return ResponseEntity.noContent().build();
    }
}