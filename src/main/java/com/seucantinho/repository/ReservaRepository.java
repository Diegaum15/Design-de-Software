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

    // 🔥 Método que o Service está chamando
    List<Reserva> findByClienteIdUsuario(String idUsuario);

    /**
     * Verifica se existe alguma reserva CONFIRMADA que se sobreponha
     * ao intervalo solicitado.
     */
    @Query("SELECT r FROM Reserva r " +
           "WHERE r.espaco.idEspaco = :idEspaco " +
           "AND r.status = 'CONFIRMADA' " +
           "AND (" +
           "   (:dataInicio < r.dataFim AND :dataFim > r.dataInicio)" +
           ")")
    List<Reserva> findSobreposicaoDeReserva(
            @Param("idEspaco") String idEspaco,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);
}
