package com.seucantinho.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 
import jakarta.persistence.*;

/**
 * Entidade Cliente, que estende a Entidade Usuario com dados específicos.
 * Usa um mapeamento One-to-One e compartilha a chave primária.
 */
@Entity
@Table(name = "clientes")
// Ignora apenas campos internos do Hibernate (OK para GET)
// NÃO IGNORA "usuario", para permitir desserialização no POST
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cliente {

    @Id
    @Column(name = "id_usuario")
    private String idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(length = 255)
    private String endereco;

    public Cliente() {}

    // Getters e Setters

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;

        // Garante sincronização do ID
        if (usuario != null) {
            this.idUsuario = usuario.getIdUsuario();
        }
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
