package com.seucantinho.repository;

import com.seucantinho.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    // Métodos CRUD são herdados.
    
    // Uma consulta essencial para autenticação/login:
    Usuario findByEmail(String email);
    
    // Note: Use este repositório para operações gerais de Usuario.
    // Para operações específicas de Cliente ou Administrador, criaremos seus próprios repositórios.
}