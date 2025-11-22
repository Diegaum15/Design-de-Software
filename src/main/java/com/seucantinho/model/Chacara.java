package com.seucantinho.model;

import jakarta.persistence.*;
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
    private boolean temPiscina;
    private int numQuartos;
    private String areaLazer;
    private int estacionamentoCapacidade;
}