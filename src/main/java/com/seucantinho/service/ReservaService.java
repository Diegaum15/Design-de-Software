package com.seucantinho.service;

import com.seucantinho.model.Reserva;
import com.seucantinho.model.Cliente;
import com.seucantinho.model.Espaco;
import com.seucantinho.repository.ReservaRepository;
import com.seucantinho.exception.ReservaIndisponivelException;
import com.seucantinho.exception.ValidacaoException;
import com.seucantinho.exception.UsuarioNaoEncontradoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteService clienteService;
    private final EspacoService espacoService;

    @Autowired
    public ReservaService(
        ReservaRepository reservaRepository,
        ClienteService clienteService,
        EspacoService espacoService
    ) {
        this.reservaRepository = reservaRepository;
        this.clienteService = clienteService;
        this.espacoService = espacoService;
    }

    // ------------------------------------------------------------------------
    // MÉTODOS PRINCIPAIS
    // ------------------------------------------------------------------------

    /**
     * Cria uma nova Reserva, aplicando todas as regras de validação.
     * @param reserva A entidade Reserva a ser criada.
     * @return A Reserva salva no estado PENDENTE.
     */
    public Reserva criarReserva(Reserva reserva) {

        Cliente cliente = clienteService.buscarPorId(reserva.getCliente().getIdUsuario());
        Espaco espaco = espacoService.buscarPorId(reserva.getEspaco().getIdEspaco());

        reserva.setCliente(cliente);
        reserva.setEspaco(espaco);

        if (reserva.getDataEvento() == null) {
            throw new ValidacaoException("A data do evento é obrigatória.");
        }

        if (reserva.getDataEvento().isBefore(LocalDateTime.now())) {
            throw new ValidacaoException("A data do evento não pode ser no passado.");
        }

        // verifica conflito: evento no mesmo instante
        List<Reserva> conflitos = reservaRepository.verificarConflitos(
                espaco.getIdEspaco(),
                reserva.getDataEvento()
        );

        if (!conflitos.isEmpty()) {
            throw new ReservaIndisponivelException("O espaço já está reservado neste horário.");
        }

        reserva.setDataReserva(LocalDateTime.now());
        reserva.setStatusReserva("PENDENTE");

        return reservaRepository.save(reserva);
    }

    
    /**
     * Atualiza o status da reserva para CONFIRMADA após a confirmação do pagamento.
     * (Este método será chamado pelo PagamentoService)
     * @param idReserva ID da reserva.
     * @return A Reserva atualizada.
     */
    public Reserva confirmarReserva(String idReserva) {
        Reserva reserva = buscarPorId(idReserva);
        
        if (reserva.getStatusReserva().equals("PENDENTE")) {
            reserva.setStatusReserva("CONFIRMADA");
            return reservaRepository.save(reserva);
        }
        
        throw new ValidacaoException("A reserva não pode ser confirmada pois já está no status: " + reserva.getStatusReserva());
    }

    /**
     * Busca uma Reserva pelo ID.
     * @param idReserva O ID da reserva.
     * @return A Reserva encontrada.
     * @throws EntityNotFoundException se a reserva não for encontrada.
     */
    public Reserva buscarPorId(String idReserva) {
        return reservaRepository.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva com ID " + idReserva + " não encontrada."));
    }

    /**
     * Lista todas as reservas de um cliente específico.
     * @param idCliente ID do cliente.
     * @return Lista de Reservas do cliente.
     */
    public List<Reserva> listarReservasPorCliente(String idCliente) {
        return reservaRepository.findByClienteIdUsuario(idCliente);
    }
}