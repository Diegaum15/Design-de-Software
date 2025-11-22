package com.seucantinho.repository;

import com.seucantinho.model.Espaco;
import com.seucantinho.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, String> {
    
    // Método crucial para a lógica de negócio: Verificar se há reservas conflitantes.
    // Esta consulta verifica se já existe uma reserva confirmada para o mesmo espaço
    // que se sobrepõe ao período de interesse (dataInicio, dataFim).
    @Query("""
        SELECT r FROM Reserva r
        WHERE r.espaco.idEspaco = :idEspaco
        AND r.dataEvento = :dataEvento
        AND r.statusReserva = 'CONFIRMADA'
    """)
    List<Reserva> verificarConflitos(
            @Param("idEspaco") String idEspaco,
            @Param("dataEvento") LocalDateTime dataEvento
    );
}