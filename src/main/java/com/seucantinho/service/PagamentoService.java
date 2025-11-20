package com.seucantinho.service;

import com.seucantinho.model.Pagamento;
import com.seucantinho.model.Reserva;
import com.seucantinho.repository.PagamentoRepository;
import com.seucantinho.exception.ValidacaoException;
import com.seucantinho.dto.PagamentoRequest; // Assumimos que o DTO PagamentoRequest existe
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
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
        // 1. Buscar a Reserva associada
        Reserva reserva = reservaService.buscarPorId(idReserva);

        // 2. Validação básica de status
        if (!reserva.getStatusReserva().equals("PENDENTE")) {
            throw new ValidacaoException("A reserva já foi processada ou está em status " + reserva.getStatusReserva());
        }

        // 3. Simulação da Transação (em um sistema real, chamaria o gateway)
        boolean sucesso = simularGatewayPagamento(request.getNumeroCartao(), reserva.getValorPago()); 

        // 4. Criação da Entidade Pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setReserva(reserva);
        pagamento.setValor(reserva.getValorPago());
        pagamento.setDataPagamento(LocalDateTime.now());

        if (sucesso) {
            pagamento.setStatus("QUITADO");
            
            // 5. Atualiza o status da Reserva para CONFIRMADA
            reservaService.confirmarReserva(idReserva);
        } else {
            pagamento.setStatus("FALHOU");
            throw new ValidacaoException("Falha ao processar o pagamento. Tente novamente ou use outro método.");
        }

        // 6. Salvar o registro de Pagamento
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
        if (numeroCartao != null && numeroCartao.startsWith("4") && !numeroCartao.equals("4444")) {
            // Em um ambiente real, você faria aqui a chamada HTTP/SDK para Stripe, PagSeguro, etc.
            System.out.println("Transação de R$" + valor + " APROVADA.");
            return true; 
        }
        System.out.println("Transação REJEITADA.");
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