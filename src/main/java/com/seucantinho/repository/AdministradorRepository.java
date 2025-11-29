package com.seucantinho.repository;

import com.seucantinho.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, String> {
    
    Administrador findByMatricula(String matricula);
}