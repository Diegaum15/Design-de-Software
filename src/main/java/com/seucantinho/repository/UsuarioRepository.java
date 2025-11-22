package com.seucantinho.repository;

import com.seucantinho.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Usuario.
 * Herda operações CRUD e métodos de busca personalizados.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    boolean existsByEmail(String email);

    // ADICIONE ESTE:
    Optional<Usuario> findByEmail(String email);
}
