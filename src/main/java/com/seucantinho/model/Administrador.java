package com.seucantinho.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "TB_ADMINISTRADOR")
@PrimaryKeyJoinColumn(name = "idUsuario")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Administrador extends Usuario {

    @Column(nullable = false, unique = true)
    private String matricula;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "TB_ADMINISTRADOR_FILIAL",
            joinColumns = @JoinColumn(name = "idAdministrador"),
            inverseJoinColumns = @JoinColumn(name = "idFilial")
    )
    private Set<Filial> filiaisGerenciadas = new HashSet<>();
}
