
package com.seucantinho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "TB_FILIAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idFilial;
    
    @Column(nullable = false)
    private String nomeFilial;
    
    private String endereco;
    private String telefone;
    
    private boolean status;
    
    // Relacionamento 1:N com Espacos (Uma Filial tem muitos Espaços)
    @OneToMany(mappedBy = "filial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Espaco> espacos;
}