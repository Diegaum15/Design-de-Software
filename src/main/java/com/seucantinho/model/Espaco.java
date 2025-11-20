package com.seucantinho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ESPACO")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Espaco {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idEspaco;
    
    @Column(nullable = false)
    private String nome;
    
    // O tipo é inferido pela herança, mas podemos mantê-lo para consultas rápidas
    private String tipo; 
    
    private int capacidade;
    private float preco;
    private String foto;

    // Relacionamento N:1 com Filial (Muitos Espaços pertencem a uma Filial)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_filial", nullable = false)
    private Filial filial;
}

// Salao.java
@Entity
@Table(name = "TB_SALAO")
@PrimaryKeyJoinColumn(name = "idEspaco")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salao extends Espaco {
    private String tamanhoCozinha;
    private int quantidadeCadeiras;
    private float areaTotal;
}

// Chacara.java
@Entity
@Table(name = "TB_CHACARA")
@PrimaryKeyJoinColumn(name = "idEspaco")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chacara extends Espaco {
    private boolean temPiscina;
    private int numQuartos;
    private String areaLazer;
    private int estacionamentoCapacidade;
}

// QuadraEsportiva.java
@Entity
@Table(name = "TB_QUADRA_ESPORTIVA")
@PrimaryKeyJoinColumn(name = "idEspaco")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuadraEsportiva extends Espaco {
    private String tipoPiso;
    private String tipoEsportes;
}