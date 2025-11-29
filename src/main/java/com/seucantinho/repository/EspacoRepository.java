// com.seucantinho.repository/EspacoRepository.java
package com.seucantinho.repository;

import com.seucantinho.model.Espaco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspacoRepository extends JpaRepository<Espaco, String> {
    // Métodos de busca específicos, se necessário
}