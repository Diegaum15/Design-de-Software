package com.seucantinho.repository;

import com.seucantinho.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, String> {
    
    // Método para buscar um pagamento pela sua reserva associada
    Pagamento findByReservaIdReserva(String idReserva);
    
    // Método para listar todos os pagamentos com um status específico (ex: 'QUITADO', 'FALHOU')
    // List<Pagamento> findByStatus(String status);
}