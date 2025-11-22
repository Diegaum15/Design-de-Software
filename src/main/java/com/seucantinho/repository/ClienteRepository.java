package com.seucantinho.repository;

import com.seucantinho.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    boolean existsByCpf(String cpf);

    // Agora sim: retorna Optional<Cliente>
    Optional<Cliente> findByCpf(String cpf);
}
