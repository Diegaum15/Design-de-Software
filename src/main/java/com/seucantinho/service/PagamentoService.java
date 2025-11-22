package com.seucantinho.service;

import com.seucantinho.model.Pagamento;
import com.seucantinho.model.Reserva;
import com.seucantinho.repository.PagamentoRepository;
import com.seucantinho.exception.ValidacaoException;
import com.seucantinho.dto.PagamentoRequest; 
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    // CRÍTICO: O serviço precisa interagir com o ReservaService para buscar e atualizar a Reserva.
    private final ReservaService reservaService; 

    @Autowired
    public PagamentoService(
        PagamentoRepository pagamentoRepository,
        ReservaService reservaService
    ) {
        this.pagamentoRepository = pagamentoRepository;
        this.reservaService = reservaService;
    }

    // ------------------------------------------------------------------------
    // MÉTODOS PRINCIPAIS
    // ------------------------------------------------------------------------

    /**
     * Processa o pagamento de uma Reserva e atualiza o status.
     * Esta é a simulação da integração com um gateway de pagamento.
     * @param idReserva O ID da reserva a ser paga.
     * @param request Os dados de pagamento (simulados).
     * @return O Pagamento registrado.
     */
    public Pagamento processarPagamento(String idReserva, PagamentoRequest request) {
        // 1. Buscar a Reserva associada (garante que a reserva exista)
        Reserva reserva = reservaService.buscarPorId(idReserva); 

        // 2. Validação básica de status
        if (!reserva.getStatusReserva().equals("PENDENTE")) {
            throw new ValidacaoException("A reserva já foi processada ou está em status " + reserva.getStatusReserva());
        }
        
        // 3. Verifica se já existe um pagamento para esta reserva (Evita duplicidade)
        if (pagamentoRepository.findByReservaIdReserva(idReserva) != null) {
            throw new ValidacaoException("Já existe um pagamento registrado para esta reserva.");
        }

        // 4. Simulação da Transação
        boolean sucesso = simularGatewayPagamento(request.getNumeroCartao(), reserva.getValorPago()); 

        // 5. Criação da Entidade Pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setReserva(reserva);
        pagamento.setValor(reserva.getValorPago());
        pagamento.setDataPagamento(LocalDateTime.now());

        if (sucesso) {
            pagamento.setStatus("QUITADO");
            
            // 6. Atualiza o status da Reserva para CONFIRMADA
            reservaService.confirmarReserva(idReserva); // Assumindo que este método existe no ReservaService
        } else {
            pagamento.setStatus("FALHOU");
            // Se falhou, salva o registro de falha (para auditoria) e lança exceção para o Controller
            Pagamento pagamentoFalhou = pagamentoRepository.save(pagamento);
            throw new ValidacaoException("Falha ao processar o pagamento. Transação " + pagamentoFalhou.getIdPagamento + " falhou.");
        }

        // 7. Salvar o registro de Pagamento bem-sucedido
        return pagamentoRepository.save(pagamento);
    }

    /**
     * Simulação de uma chamada a um gateway de pagamento externo.
     * @param numeroCartao Número do cartão (simulado)
     * @param valor Valor a ser cobrado
     * @return true se o pagamento foi bem-sucedido.
     */
    private boolean simularGatewayPagamento(String numeroCartao, float valor) {
        // Lógica de simulação:
        // Assume que qualquer cartão que comece com '4' e não seja '4444' é válido.
        if (numeroCartao != null && numeroCartao.startsWith("4") && !numeroCartao.equals("4444000000000000")) {
            System.out.println("Simulação de Transação: R$" + valor + " APROVADA.");
            return true; 
        }
        System.out.println("Simulação de Transação: R$" + valor + " REJEITADA.");
        return false;
    }

    /**
     * Busca um Pagamento pelo ID.
     * @param idPagamento O ID do pagamento.
     * @return O Pagamento encontrado.
     * @throws EntityNotFoundException se o pagamento não for encontrado.
     */
    public Pagamento buscarPorId(String idPagamento) {
        return pagamentoRepository.findById(idPagamento)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento com ID " + idPagamento + " não encontrado."));
    }
}