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
@Table(name = "TB_CHACARA")
@PrimaryKeyJoinColumn(name = "idEspaco")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Chacara extends Espaco {

    // Campos específicos para Chácara
    @Column(nullable = true)
    private Boolean temPiscina;

    @Column(nullable = true)
    private Integer numQuartos;
    
    @Column(nullable = true)
    private String areaLazer; 

    @Column(nullable = true)
    private Integer estacionamentoCapacidade;
}