package com.seucantinho.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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

// ------------------------------------------------------------------------
// CONFIGURAÇÃO JACKSON PARA HERANÇA (CORRIGE O ERRO 500)
// Permite que o Jackson saiba qual subclasse deve ser instanciada.
// Ele procurará pelo campo "tipo" no JSON.
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, // Usa o nome da classe ou um alias
        include = JsonTypeInfo.As.PROPERTY, // O discriminador estará em um campo
        property = "tipo" // O nome do campo JSON que contém o tipo (ex: "SALAO")
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Salao.class, name = "SALAO"),
        @JsonSubTypes.Type(value = Chacara.class, name = "CHACARA"),
        @JsonSubTypes.Type(value = QuadraEsportiva.class, name = "QUADRAESPORTIVA")
})
// ------------------------------------------------------------------------
public abstract class Espaco {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idEspaco;
    
    @Column(nullable = false)
    private String nome;
    
    // Este campo não é persistido, mas é usado como discriminador pelo Jackson
    // para indicar o tipo durante a desserialização do JSON.
    @Transient 
    private String tipo; 
    
    private int capacidade;
    private float preco;
    private String foto;

    // Relacionamento N:1 com Filial (Muitos Espaços pertencem a uma Filial)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_filial", nullable = false)
    private Filial filial;
}