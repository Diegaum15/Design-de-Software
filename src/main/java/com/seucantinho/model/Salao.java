package com.seucantinho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
// --- IMPORTAÇÕES FALTANTES ---
import lombok.Getter; 
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
// -----------------------------

@Entity
@Table(name = "TB_SALAO")
@PrimaryKeyJoinColumn(name = "idEspaco")
@Getter 
@Setter
@ToString
@EqualsAndHashCode(callSuper = true) // Agora chama o equals/hashCode da classe Espaco
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Salao extends Espaco {
    private String tamanhoCozinha;
    private int quantidadeCadeiras;
    private float areaTotal;
}