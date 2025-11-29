package com.seucantinho.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "TB_SALAO")
@PrimaryKeyJoinColumn(name = "idEspaco") // Chave Primária herdada de TB_ESPACO
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Salao extends Espaco {

    // Campos específicos para Salão de Festas
    @Column(nullable = true)
    private String tamanhoCozinha; // Ex: "Pequena", "Média", "Grande"

    @Column(nullable = true)
    private Integer quantidadeCadeiras;

    @Column(nullable = true)
    private Float areaTotal; // Em metros quadrados
}