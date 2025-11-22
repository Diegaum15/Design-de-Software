package com.seucantinho.controller;

import com.seucantinho.dto.EspacoDTO;
import com.seucantinho.exception.ValidacaoException;
import com.seucantinho.model.*;
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
@Tag(name = "Espaços", description = "Gerenciamento e consulta de espaços de aluguel.")
public class EspacoController {

    private final EspacoService espacoService;

    @Autowired
    public EspacoController(EspacoService espacoService) {
        this.espacoService = espacoService;
    }

    // ------------------------------------------------------------------------
    // CREATE (POST)
    // ------------------------------------------------------------------------
    @Operation(summary = "Cria um novo espaço")
    @PostMapping
    public ResponseEntity<Espaco> criarEspaco(@Valid @RequestBody EspacoDTO dto) {

        Espaco espaco = convertDtoToModel(dto);

        Espaco novo = espacoService.salvarEspaco(espaco);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    // ------------------------------------------------------------------------
    // READ (GET by ID)
    // ------------------------------------------------------------------------
    @Operation(summary = "Busca um espaço pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<Espaco> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(espacoService.buscarPorId(id));
    }

    // ------------------------------------------------------------------------
    // UPDATE (PUT)
    // ------------------------------------------------------------------------
    @Operation(summary = "Atualiza um espaço existente")
    @PutMapping("/{id}")
    public ResponseEntity<Espaco> atualizarEspaco(
            @PathVariable String id,
            @Valid @RequestBody EspacoDTO dto) {

        Espaco espacoAtualizado = convertDtoToModel(dto);

        Espaco atualizado = espacoService.atualizarEspaco(id, espacoAtualizado);

        return ResponseEntity.ok(atualizado);
    }

    // ------------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------------
    @Operation(summary = "Deleta um espaço (verifica reservas futuras)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEspaco(@PathVariable String id) {
        espacoService.deletarEspaco(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------------
    // LISTAGEM E DISPONIBILIDADE
    // ------------------------------------------------------------------------
    @Operation(summary = "Lista espaços ou filtra por tipo e disponibilidade")
    @GetMapping
    public ResponseEntity<List<Espaco>> listarEspacos(
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataFim,
            @RequestParam(required = false) String tipo
    ) {
        if (dataInicio != null && dataFim != null) {
            return ResponseEntity.ok(espacoService.listarDisponiveis(dataInicio, dataFim, tipo));
        }

        return ResponseEntity.ok(espacoService.listarTodos());
    }

    // ------------------------------------------------------------------------
    // Conversão DTO → Model (Suporta CREATE + UPDATE)
    // ------------------------------------------------------------------------
    private Espaco convertDtoToModel(EspacoDTO dto) {

        Espaco espaco;

        switch (dto.getTipo().toUpperCase()) {
            case "SALAO":
                espaco = new Salao();
                ((Salao) espaco).setTamanhoCozinha(dto.getTamanhoCozinha());
                ((Salao) espaco).setQuantidadeCadeiras(dto.getQuantidadeCadeiras());
                ((Salao) espaco).setAreaTotal(dto.getAreaTotal());
                break;

            case "CHACARA":
                espaco = new Chacara();
                ((Chacara) espaco).setTemPiscina(dto.getTemPiscina());
                ((Chacara) espaco).setNumQuartos(dto.getNumQuartos());
                ((Chacara) espaco).setAreaLazer(dto.getAreaLazer());
                ((Chacara) espaco).setEstacionamentoCapacidade(dto.getEstacionamentoCapacidade());
                break;

            case "QUADRAESPORTIVA":
                espaco = new QuadraEsportiva();
                ((QuadraEsportiva) espaco).setTipoPiso(dto.getTipoPiso());
                ((QuadraEsportiva) espaco).setTipoEsportes(dto.getTipoEsportes());
                break;

            default:
                throw new ValidacaoException("Tipo de espaço inválido: " + dto.getTipo());
        }

        // Campos comuns
        espaco.setIdEspaco(dto.getIdEspaco());
        espaco.setNome(dto.getNome());
        espaco.setTipo(dto.getTipo());
        espaco.setCapacidade(dto.getCapacidade());
        espaco.setPreco(dto.getPreco());
        espaco.setFoto(dto.getFoto());

        // Relacionamento com Filial
        Filial filial = new Filial();
        filial.setIdFilial(dto.getIdFilial());
        espaco.setFilial(filial);

        return espaco;
    }
}
