package com.seucantinho.repository;

import com.seucantinho.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, String> {

    // Busca reservas por cliente
    List<Reserva> findByClienteIdUsuario(String idUsuario);

    /**
     * Verifica se existe alguma reserva CONFIRMADA que se sobreponha ao período solicitado.
     * A lógica de sobreposição é:
     *
     *   (início solicitado < fim da reserva existente)
     *   AND
     *   (fim solicitado > início da reserva existente)
     *
     */
    @Query("""
        SELECT r
        FROM Reserva r
        WHERE r.espaco.idEspaco = :idEspaco
          AND r.statusReserva = 'CONFIRMADA'
          AND (
                :dataInicio < r.dataEventoFim
            AND :dataFim > r.dataEvento
          )
    """)
    List<Reserva> findSobreposicaoDeReserva(
            @Param("idEspaco") String idEspaco,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
