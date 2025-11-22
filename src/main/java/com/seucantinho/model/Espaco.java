package com.seucantinho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "TB_ESPACO")
@Inheritance(strategy = InheritanceType.JOINED)
@Data // Mantido para Getters/Setters
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@SuperBuilder // Adicionado para suportar herança de construtores
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