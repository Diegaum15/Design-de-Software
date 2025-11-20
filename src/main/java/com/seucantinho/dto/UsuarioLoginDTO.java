package com.seucantinho.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Gera Getters, Setters, ToString, EqualsAndHashCode
public class UsuarioLoginDTO {

    @NotBlank(message = "O email é obrigatório para o login.")
    @Email(message = "Formato de email inválido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória para o login.")
    private String senha;
}