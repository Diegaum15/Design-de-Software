package com.seucantinho.repository;

import com.seucantinho.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, String> {
    // Espaco para métodos de consulta personalizados, se necessário
}