package com.seucantinho.controller;

import com.seucantinho.dto.EspacoDTO;
import com.seucantinho.service.EspacoService;
// Remova os imports desnecessários de Entidades (Espaco, Salao, etc.) e ValidacaoException
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

    @Operation(summary = "Cria ou atualiza um espaço",
               description = "Registra um novo espaço (Salão, Chácara ou Quadra Esportiva) ou atualiza um existente. Usa o EspacoDTO.")
    @PostMapping
    // Altera a assinatura para receber e retornar EspacoDTO
    public ResponseEntity<EspacoDTO> salvarEspaco(@Valid @RequestBody EspacoDTO espacoDTO) {
        
        // O Service cuida da conversão, validação e persistência
        EspacoDTO novoEspaco = espacoService.salvarEspaco(espacoDTO);
        
        // Retorna 201 Created se o ID estava nulo, ou 200 OK se o ID já existia (atualização)
        HttpStatus status = (espacoDTO.getIdEspaco() == null) ? HttpStatus.CREATED : HttpStatus.OK;
        return new ResponseEntity<>(novoEspaco, status);
    }
    
    @Operation(summary = "Busca um espaço pelo ID")
    @GetMapping("/{id}")
    // Altera a assinatura para retornar EspacoDTO
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
    // Altera a assinatura para retornar List<EspacoDTO>
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

    // REMOVA O MÉTODO convertDtoToModel AQUI, POIS ELE FOI MOVIDO PARA O SERVICE
}