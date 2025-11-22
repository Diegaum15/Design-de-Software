package com.seucantinho.repository;

import com.seucantinho.model.Espaco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspacoRepository extends JpaRepository<Espaco, String> {
    // Métodos CRUD são herdados.
    
    // Consultas úteis para a lógica de negócio (busca por disponibilidade ou tipo):
    
    // Lista todos os espaços de um determinado tipo (ex: QuadraEsportiva)
    // List<Espaco> findByTipo(String tipo);
    
    // Lista todos os espaços em uma filial específica
    // List<Espaco> findByFilialIdFilial(String idFilial);
    List<Espaco> findByTipoIgnoreCase(String tipo);

}