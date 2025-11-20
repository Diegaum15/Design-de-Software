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
        // 1. Validação de Cliente e Espaço
        Cliente cliente = clienteService.buscarPorId(reserva.getCliente().getIdUsuario());
        Espaco espaco = espacoService.buscarPorId(reserva.getEspaco().getIdEspaco());
        
        // Atribui as entidades gerenciadas de volta à reserva para garantir a persistência correta
        reserva.setCliente(cliente);
        reserva.setEspaco(espaco);

        // 2. Validação de Datas
        if (reserva.getDataEvento() == null || reserva.getDataReserva() == null) {
            throw new ValidacaoException("Datas de evento e reserva são obrigatórias.");
        }
        if (reserva.getDataEvento().isBefore(LocalDateTime.now())) {
            throw new ValidacaoException("A data do evento não pode ser no passado.");
        }
        
        // 3. Validação de Disponibilidade
        // Chamamos a lógica de disponibilidade que está encapsulada no EspacoService
        if (!espacoService.verificarDisponibilidade(
                espaco.getIdEspaco(), 
                reserva.getDataEvento(), 
                reserva.getDataEvento().plusHours(4) // Exemplo: Assumindo um evento de 4h
            )) 
        {
            throw new ReservaIndisponivelException("O espaço não está disponível para o período solicitado.");
        }
        
        // 4. Configuração Inicial da Reserva
        reserva.setDataReserva(LocalDateTime.now());
        reserva.setStatusReserva("PENDENTE"); // Inicia como pendente até o pagamento
        
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