package com.seucantinho.controller;

import com.seucantinho.dto.EspacoDTO;
import com.seucantinho.service.EspacoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/espacos")
@Tag(name = "Espaços", description = "Gerenciamento e consulta de Salões, Chácaras e Quadras Esportivas.")
public class EspacoController {

    private final EspacoService espacoService;

    @Autowired
    public EspacoController(EspacoService espacoService) {
        this.espacoService = espacoService;
    }

    // ------------------------------------------------------------------------
    // CRUD BÁSICO (Administrativo)
    // ------------------------------------------------------------------------

    @Operation(summary = "Cria um novo espaço",
               description = "Registra um novo espaço (Salão, Chácara ou Quadra Esportiva).")
    @PostMapping
    public ResponseEntity<EspacoDTO> criarEspaco(@Valid @RequestBody EspacoDTO espacoDTO) {

        // Garante que o ID é nulo para forçar a criação (embora o service já trate)
        espacoDTO.setIdEspaco(null);
        
        // O Service cuida da conversão, validação e persistência
        EspacoDTO novoEspaco = espacoService.salvarEspaco(espacoDTO);
        
        // Retorna 201 Created
        return new ResponseEntity<>(novoEspaco, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Atualiza um espaço existente",
               description = "Atualiza completamente os dados de um espaço específico. O ID no path deve corresponder ao ID no corpo.")
    @PutMapping("/{id}")
    public ResponseEntity<EspacoDTO> atualizarEspaco(
            @PathVariable String id, 
            @Valid @RequestBody EspacoDTO espacoDTO) {

        // Regra de segurança/coerência: o ID do path deve ser usado para a atualização
        if (espacoDTO.getIdEspaco() == null || !id.equals(espacoDTO.getIdEspaco())) {
            // Se o ID no corpo estiver faltando ou for diferente do path
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); 
        }
        
        // O Service cuida da conversão, validação e persistência
        EspacoDTO espacoAtualizado = espacoService.salvarEspaco(espacoDTO);
        
        // Retorna 200 OK
        return ResponseEntity.ok(espacoAtualizado);
    }

    @Operation(summary = "Busca um espaço pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<EspacoDTO> buscarPorId(@PathVariable String id) {
        EspacoDTO espaco = espacoService.buscarPorId(id);
        return ResponseEntity.ok(espaco);
    }
    
    @Operation(summary = "Deleta um espaço",
               description = "Deleta permanentemente um espaço pelo ID. Deve verificar reservas pendentes.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEspaco(@PathVariable String id) {
        espacoService.deletarEspaco(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------------
    // CONSULTA E DISPONIBILIDADE (Público)
    // ------------------------------------------------------------------------
    
    @Operation(summary = "Lista todos os espaços ou filtra por tipo e disponibilidade")
    @GetMapping
    public ResponseEntity<List<EspacoDTO>> listarEspacos(
            @Parameter(description = "Data e hora de início da reserva (formato ISO: YYYY-MM-DDTHH:MM:SS)")
            @RequestParam(required = false) LocalDateTime dataInicio,
            
            @Parameter(description = "Data e hora de fim da reserva (formato ISO: YYYY-MM-DDTHH:MM:SS)")
            @RequestParam(required = false) LocalDateTime dataFim,
            
            @Parameter(description = "Filtra por tipo de espaço (e.g., Salao, Chacara)")
            @RequestParam(required = false) String tipo) {
        
        List<EspacoDTO> resultados;

        // Se as datas de disponibilidade forem fornecidas, usamos a lógica de filtro.
        if (dataInicio != null && dataFim != null) {
            resultados = espacoService.listarDisponiveis(dataInicio, dataFim, tipo);
        } else {
            // Se nenhuma data for fornecida, lista todos
            resultados = espacoService.listarTodos();
        }
        
        return ResponseEntity.ok(resultados);
    }
}