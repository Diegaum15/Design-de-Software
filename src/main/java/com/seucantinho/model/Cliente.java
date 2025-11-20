package com.seucantinho.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_CLIENTE")
@PrimaryKeyJoinColumn(name = "idUsuario") // Usa a chave primária da superclasse (TB_USUARIO)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Usuario {
    
    @Column(nullable = false, unique = true)
    private String cpf;
    
    private String endereco;
}