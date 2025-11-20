package com.seucantinho.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data // Gera Getters, Setters, ToString, EqualsAndHashCode
public class EspacoDTO {

    // ------------------------------------------------------------------------
    // Campos Comuns (Superclasse Espaco)
    // ------------------------------------------------------------------------
    
    // Opcional para cadastro (será gerado), mas útil para atualização e listagem
    private String idEspaco; 

    @NotBlank(message = "O nome do espaço é obrigatório.")
    private String nome;

    @NotBlank(message = "O ID da filial associada é obrigatório.")
    private String idFilial;

    @NotBlank(message = "O tipo do espaço (Salao, Chacara, QuadraEsportiva) é obrigatório.")
    private String tipo;

    @NotNull(message = "A capacidade máxima é obrigatória.")
    @Min(value = 1, message = "A capacidade deve ser no mínimo 1.")
    private Integer capacidade;

    @NotNull(message = "O preço da diária/hora é obrigatório.")
    private Float preco;
    
    private String foto; // URL ou nome do arquivo

    // ------------------------------------------------------------------------
    // Campos Específicos (Subclasses - Opcionais)
    // ------------------------------------------------------------------------
    
    // Salao
    private String tamanhoCozinha;
    private Integer quantidadeCadeiras;
    private Float areaTotal;

    // Chacara
    private Boolean temPiscina;
    private Integer numQuartos;
    private String areaLazer;
    private Integer estacionamentoCapacidade;

    // QuadraEsportiva
    private String tipoPiso;
    private String tipoEsportes;
}