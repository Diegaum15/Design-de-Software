package com.seucantinho.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_USUARIO")
@Inheritance(strategy = InheritanceType.JOINED) // Define o tipo de herança
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Usuario { // Classe abstrata para garantir que apenas Cliente/Admin sejam instanciados

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idUsuario;

    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String senha;
    
    private String telefone;
    
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro = LocalDateTime.now();
    
    // Não precisa de 'tipoUsuario' se estiver usando herança (JPA infere o tipo)
    
    private boolean status = true;
}