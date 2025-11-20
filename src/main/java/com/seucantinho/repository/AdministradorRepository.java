package com.seucantinho.repository;

import com.seucantinho.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, String> {
    
    // Métodos CRUD básicos herdados.
    
    // Podemos adicionar consultas para buscar administradores por alguma filial gerenciada, se necessário:
    // List<Administrador> findByFiliaisGerenciadas_IdFilial(String idFilial);
}