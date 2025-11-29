package com.seucantinho.repository;

import com.seucantinho.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
    
    // Método para buscar um cliente pelo CPF (essencial para cadastro e validação)
    Cliente findByCpf(String cpf);

    // O método findByEmail(String email) também é herdável, mas é mais limpo defini-lo no UsuarioRepository.
}