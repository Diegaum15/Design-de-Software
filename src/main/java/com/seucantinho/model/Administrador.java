package com.seucantinho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "TB_ADMINISTRADOR")
@PrimaryKeyJoinColumn(name = "idUsuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrador extends Usuario {

    // Relacionamento M:N (Um Admin pode gerenciar N filiais e uma filial pode ter N admins)
    // É um relacionamento mais complexo. Se for 1:N (Admin gerencia uma ou poucas), use List.
    // Usaremos M:N, que é mais realista para "filiais gerenciadas".
    @ManyToMany
    @JoinTable(
        name = "admin_filial",
        joinColumns = @JoinColumn(name = "id_administrador"),
        inverseJoinColumns = @JoinColumn(name = "id_filial")
    )
    private List<Filial> filiaisGerenciadas;
}