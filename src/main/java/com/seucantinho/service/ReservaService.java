package com.seucantinho.service;

import com.seucantinho.model.Reserva;
import com.seucantinho.model.Cliente;
import com.seucantinho.model.Espaco;
import com.seucantinho.dto.ReservaResponse;
import com.seucantinho.repository.ReservaRepository;
import com.seucantinho.exception.ReservaIndisponivelException;
import com.seucantinho.exception.ValidacaoException;
import com.seucantinho.exception.UsuarioNaoEncontradoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; 

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteService clienteService;
    private final EspacoService espacoService;
    private final PagamentoService pagamentoService;

    @Autowired
    public ReservaService(
        ReservaRepository reservaRepository,
        ClienteService clienteService,
        EspacoService espacoService,
        @Lazy PagamentoService pagamentoService
    ) {
        this.reservaRepository = reservaRepository;
        this.clienteService = clienteService;
        this.espacoService = espacoService;
        this.pagamentoService = pagamentoService;
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
        Cliente cliente = clienteService.buscarEntityPorId(reserva.getCliente().getIdUsuario());
        Espaco espaco = espacoService.buscarEntityPorId(reserva.getEspaco().getIdEspaco());

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
    
    public Reserva atualizarStatusReserva(String idReserva, String novoStatus) { 
        Reserva reserva = buscarPorId(idReserva);

        if (reserva.getStatusReserva().equals("PENDENTE") || reserva.getStatusReserva().equals("SINAL_PAGO")) {
             // Aceita a transição para estados de pagamento/cancelamento
            if (novoStatus.equals("SINAL_PAGO") || novoStatus.equals("QUITADA") || novoStatus.equals("CANCELADA")) {
                reserva.setStatusReserva(novoStatus);
                return reservaRepository.save(reserva);
            }
        }
        
        throw new ValidacaoException("A reserva não pode ser atualizada para o status: " + novoStatus + " a partir do status: " + reserva.getStatusReserva());
    }

        // -------------------------------------------------------
    // ATUALIZAR RESERVA (PUT)
    // -------------------------------------------------------
    public Reserva atualizarReserva(String idReserva, Reserva novaReserva) {
        Reserva reservaExistente = buscarPorId(idReserva);

        // Atualiza Cliente
        if (novaReserva.getCliente() != null) {
            reservaExistente.setCliente(
                clienteService.buscarEntityPorId(novaReserva.getCliente().getIdUsuario())
            );
        }

        // Atualiza Espaço
        if (novaReserva.getEspaco() != null) {
            reservaExistente.setEspaco(
                espacoService.buscarEntityPorId(novaReserva.getEspaco().getIdEspaco())
            );
        }

        // Atualiza datas
        if (novaReserva.getDataEvento() != null) {
            reservaExistente.setDataEvento(novaReserva.getDataEvento());
        }
        if (novaReserva.getDataEventoFim() != null) {
            reservaExistente.setDataEventoFim(novaReserva.getDataEventoFim());
        }

        // Atualiza valor
        reservaExistente.setValorPago(novaReserva.getValorPago());

        // Atualiza status
        if (novaReserva.getStatusReserva() != null) {
            reservaExistente.setStatusReserva(novaReserva.getStatusReserva());
        }

        // Salva tudo
        return reservaRepository.save(reservaExistente);
    }



// -------------------------------------------------------
// DELETAR RESERVA (DELETE)
// -------------------------------------------------------
public void deletarReserva(String idReserva) {
    Reserva reserva = buscarPorId(idReserva);

    reservaRepository.delete(reserva);
}

    
    /**
     * Busca uma Reserva pelo ID.
     * @param idReserva O ID da reserva.
     * @return A Reserva encontrada.
     * @throws EntityNotFoundException se a reserva não for encontrada.
     */
    public Reserva buscarPorId(String idReserva) { // <--- CORREÇÃO 2: Método restaurado
        return reservaRepository.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva com ID " + idReserva + " não encontrada."));
    }

    public ReservaResponse toResponse(Reserva reserva) {
        ReservaResponse response = new ReservaResponse();
        response.setIdReserva(reserva.getIdReserva());
        response.setIdCliente(reserva.getCliente().getIdUsuario());
        response.setIdEspaco(reserva.getEspaco().getIdEspaco());
        response.setNomeEspaco(reserva.getEspaco().getNome());
        response.setDataReserva(reserva.getDataReserva());
        response.setDataEvento(reserva.getDataEvento());
        response.setValorPago(reserva.getValorPago());
        response.setStatusReserva(reserva.getStatusReserva());
        
        // Adicionar status do pagamento se existir
        if (reserva.getPagamento() != null) {
            response.setStatusPagamento(reserva.getPagamento().getStatus());
        } else {
            response.setStatusPagamento("NÃO INICIADO");
        }
        return response;
    }

    public List<ReservaResponse> listarTodas() {
        return reservaRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public List<ReservaResponse> listarReservasPorCliente(String idCliente) {
        return reservaRepository.findByClienteIdUsuario(idCliente).stream()
            .map(this::toResponse) 
            .collect(Collectors.toList());
    }

    public ReservaResponse buscarPorIdDTO(String idReserva) {
        Reserva reserva = buscarPorId(idReserva); 
        return toResponse(reserva);
    }
}