package com.seucantinho.repository;

import com.seucantinho.model.Filial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilialRepository extends JpaRepository<Filial, String> {
    // Métodos CRUD (save, findById, findAll, delete) são herdados automaticamente.
    
    // Podemos adicionar consultas personalizadas aqui, se necessário. Ex:
    // Filial findByNomeFilial(String nomeFilial);
}