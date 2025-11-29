package com.seucantinho.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode; 
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "TB_CLIENTE")
@PrimaryKeyJoinColumn(name = "idUsuario") // Usa a chave primária da superclasse (TB_USUARIO)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cliente extends Usuario {
    
    @Column(nullable = false)
    
    private String endereco;
}