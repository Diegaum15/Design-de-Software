package com.seucantinho.model;

import jakarta.persistence.*;
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
    private String tipoPiso;
    private String tipoEsportes;
}