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
@Table(name = "TB_QUADRA_ESPORTIVA")
@PrimaryKeyJoinColumn(name = "idEspaco")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class QuadraEsportiva extends Espaco {

    // Campos específicos para Quadra Esportiva
    @Column(nullable = false)
    private String tipoPiso; // Ex: "Cimento", "Grama Sintética", "Madeira"

    @Column(nullable = true)
    private String tipoEsportes; // Ex: "Futebol, Vôlei"
}