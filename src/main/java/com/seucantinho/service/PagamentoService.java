package com.seucantinho.service;

import com.seucantinho.dto.PagamentoRequest;
import com.seucantinho.model.Pagamento;
import com.seucantinho.model.Reserva;
import com.seucantinho.repository.PagamentoRepository;
import com.seucantinho.exception.ValidacaoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ReservaService reservaService; // Injeção LAZY para evitar dependência circular
    // Define o percentual mínimo para ser considerado um Sinal (ex: 30%)
    private static final float PERCENTUAL_MINIMO_SINAL = 0.30f; 
    // Tolerância para comparação de floats (importante para valores monetários)
    private static final float TOLERANCIA_VALOR = 0.01f;

    @Autowired
    public PagamentoService(PagamentoRepository pagamentoRepository, @Lazy ReservaService reservaService) {
        this.pagamentoRepository = pagamentoRepository;
        this.reservaService = reservaService;
    }

    // ------------------------------------------------------------------------
    // MÉTODOS PRINCIPAIS
    // ------------------------------------------------------------------------

    /**
     * Processa o pagamento de uma Reserva e atualiza o status.
     * Esta é a simulação da integração com um gateway de pagamento.
     * @param request Os dados de pagamento (simulados).
     * @return O Pagamento registrado.
     */
    @Transactional
    public Pagamento processarPagamento(PagamentoRequest request) {
        
        // 1. Busca a Reserva para validação e ligação
        Reserva reserva = reservaService.buscarPorId(request.getIdReserva());
        float valorTotal = reserva.getValorPago(); // Consideramos este o valor total da reserva
        float valorPagamento = request.getValorPagamento();
        
        // 2. Validação da Reserva e Pagamento
        if (reserva.getStatusReserva().equals("QUITADA")) {
            throw new ValidacaoException("Reserva já está quitada.");
        }
        
        if (valorPagamento <= 0) {
            throw new ValidacaoException("O valor do pagamento deve ser positivo.");
        }
        
        // Simulação de Gateway de Pagamento
        String statusGateway = simularGateway(request); 
        
        // 3. Criação da Entidade Pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setReserva(reserva);
        pagamento.setValor(valorPagamento);
        pagamento.setDataPagamento(LocalDateTime.now());
        
        // Mapeamento do Status
        if (statusGateway.equals("SUCESSO")) {
            pagamento.setStatus("QUITADO"); // Pagamento em si é quitado

            // 4. Lógica de Sinal/Quitação da Reserva
            String novoStatusReserva;
            
            // Verifica se o valor pago quita a reserva (com tolerância para floats)
            if (Math.abs(valorPagamento - valorTotal) < TOLERANCIA_VALOR) {
                novoStatusReserva = "QUITADA";
            } 
            // Verifica se o valor pago é pelo menos o sinal (30% do total)
            else if (valorPagamento >= valorTotal * PERCENTUAL_MINIMO_SINAL) {
                 novoStatusReserva = "SINAL_PAGO";
            }
            else {
                 throw new ValidacaoException("Valor insuficiente para pagar o sinal mínimo de R$ " + (valorTotal * PERCENTUAL_MINIMO_SINAL));
            }
            
            // 5. Atualiza o status da Reserva usando o novo método genérico
            reservaService.atualizarStatusReserva(reserva.getIdReserva(), novoStatusReserva);
            
        } else {
            pagamento.setStatus("FALHOU");
            // Se falhou, a reserva permanece no status anterior (PENDENTE ou SINAL_PAGO)
        }
        
        // 6. Salva o Pagamento
        return pagamentoRepository.save(pagamento);
    }

    private String simularGateway(PagamentoRequest request) {
        // Lógica de simulação: Falha se o CVV for 000
        if (request.getCvv() != null && request.getCvv() == 0) {
            return "FALHA";
        }
        // Em um ambiente real, você faria uma chamada API aqui
        return "SUCESSO"; 
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