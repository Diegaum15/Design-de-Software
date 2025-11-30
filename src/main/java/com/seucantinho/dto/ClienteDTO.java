package com.seucantinho.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para Clientes.
 */
@Data // Usa Lombok para gerar Getters, Setters, etc.
public class ClienteDTO {

    private String idUsuario;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    @Size(min = 11, max = 14, message = "O CPF deve ter entre 11 e 14 caracteres")
    private String cpf;

    @Email(message = "E-mail inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    private String email;

    // A senha só é necessária no cadastro inicial
    private String senha; 
    
    private String telefone;
    private String endereco;
    private Boolean status; // Status do usuário

}