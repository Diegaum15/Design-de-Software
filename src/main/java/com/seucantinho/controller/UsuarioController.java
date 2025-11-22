package com.seucantinho.controller;

import com.seucantinho.dto.UsuarioLoginDTO;
import com.seucantinho.model.Usuario;
import com.seucantinho.service.UsuarioService;
import com.seucantinho.exception.ValidacaoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para login (autenticação) de Clientes e Administradores.")
public class UsuarioController {

    private final UsuarioService usuarioService;
    // Futuro: Injeção do AuthenticationManager e JWTService

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Login de Usuário",
               description = "Autentica um Cliente ou Administrador pelo email e senha. Retorna os dados do usuário se as credenciais forem válidas.")
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@Valid @RequestBody UsuarioLoginDTO loginDTO) {
        
        // 1. Buscar o usuário pelo email
        Usuario usuario = usuarioService.buscarPorEmail(loginDTO.getEmail());
        
        // 2. Validação da Senha (Simulação)
        // Em um sistema real, você usaria o PasswordEncoder.matches()
        
        /* // Lógica real com Spring Security:
        if (passwordEncoder.matches(loginDTO.getSenha(), usuario.getSenha())) {
            // Se a senha for válida:
            // Gerar Token JWT e retornar o Token
            return ResponseEntity.ok(jwtService.generateToken(usuario));
        } else {
            throw new ValidacaoException("Credenciais inválidas.");
        }
        */

        // Simulação sem Spring Security:
        if (usuario.getSenha() != null && usuario.getSenha().equals(loginDTO.getSenha())) {
            // Para o exemplo, retorna o objeto usuário (EM PRODUÇÃO, NUNCA RETORNE A SENHA!)
            usuario.setSenha(null); // Limpamos a senha antes de retornar
            return ResponseEntity.ok(usuario);
        } else {
            // Lança exceção de validação que será tratada pelo GlobalExceptionHandler como 400 Bad Request
            throw new ValidacaoException("Credenciais inválidas.");
        }
    }
}