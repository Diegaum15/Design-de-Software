package com.seucantinho.service;

import com.seucantinho.dto.PagamentoRequest;
import com.seucantinho.exception.ValidacaoException;
import com.seucantinho.model.Reserva;
import com.seucantinho.model.Pagamento;
import com.seucantinho.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PagamentoServiceTest {

    // Injeta o PagamentoService, mas usa Mocks para as dependências
    @InjectMocks
    private PagamentoService pagamentoService;

    // Simula o comportamento do repositório
    @Mock
    private PagamentoRepository pagamentoRepository;

    // Simula o comportamento do ReservaService
    @Mock
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        // Inicializa os mocks antes de cada teste
        MockitoAnnotations.openMocks(this);
    }
    
    // --- Dados Comuns ---
    private Reserva criarReserva(String status, float valor) {
        Reserva reserva = new Reserva();
        reserva.setIdReserva("RES123");
        reserva.setValorPago(valor);
        reserva.setStatusReserva(status);
        reserva.setDataReserva(LocalDateTime.now().minusDays(1));
        // Configura a Reserva para ser a mesma no Pagamento salvo
        when(reservaService.buscarPorId("RES123")).thenReturn(reserva);
        return reserva;
    }

    private PagamentoRequest criarRequest(String numeroCartao) {
        PagamentoRequest request = new PagamentoRequest();
        request.setIdReserva("RES123");
        request.setNumeroCartao(numeroCartao);
        request.setNomeNoCartao("Teste Pagamento");
        return request;
    }

    // ------------------------------------------------------------------------
    // CENÁRIOS DE SUCESSO
    // ------------------------------------------------------------------------
    
    @Test
    void processarPagamento_DeveSerBemSucedido_QuandoCartaoValidoEStatusPendente() {
        // Cenário: Reserva PENDENTE, Cartão simulado que APROVA (Ex: 4000...)
        Reserva reservaPendente = criarReserva("PENDENTE", 100.0f);
        PagamentoRequest request = criarRequest("4000111122223333"); // Cartão Válido

        // Simula o retorno do PagamentoRepository após salvar
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> {
            Pagamento p = invocation.getArgument(0);
            p.setIdPagamento("PAG456"); // Simula a geração do ID
            return p;
        });

        // Ação
        Pagamento pagamentoSalvo = pagamentoService.processarPagamento("RES123", request);

        // Verificações
        assertNotNull(pagamentoSalvo);
        assertEquals("QUITADO", pagamentoSalvo.getStatus());
        assertEquals(reservaPendente.getIdReserva(), pagamentoSalvo.getReserva().getIdReserva());

        // CRÍTICO: Verifica se o método de confirmação no ReservaService foi chamado
        verify(reservaService, times(1)).confirmarReserva("RES123");
        // Verifica se o pagamento foi salvo
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }

    // ------------------------------------------------------------------------
    // CENÁRIOS DE FALHA
    // ------------------------------------------------------------------------

    @Test
    void processarPagamento_DeveFalhar_QuandoCartaoInvalido() {
        // Cenário: Reserva PENDENTE, Cartão simulado que REJEITA (Ex: 4444...)
        criarReserva("PENDENTE", 100.0f);
        PagamentoRequest request = criarRequest("4444000000000000"); // Cartão Inválido (Conforme sua simulação)

        // Simula o salvamento de um pagamento com status FALHOU para auditoria
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> {
            Pagamento p = invocation.getArgument(0);
            p.setIdPagamento("PAG_FAIL"); 
            return p;
        });

        // Ação e Verificação: Deve lançar uma ValidacaoException
        ValidacaoException exception = assertThrows(ValidacaoException.class, () -> 
            pagamentoService.processarPagamento("RES123", request)
        );

        // Verifica se a exceção contém a mensagem de falha de pagamento
        assertTrue(exception.getMessage().contains("Falha ao processar o pagamento"));
        
        // CRÍTICO: Verifica se o ReservaService NUNCA foi chamado para confirmar
        verify(reservaService, never()).confirmarReserva(anyString());
        // Verifica se o pagamento de FALHA foi salvo para auditoria
        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
    }
    
    @Test
    void processarPagamento_DeveFalhar_QuandoReservaJaConfirmada() {
        // Cenário: Reserva CONFIRMADA, Cartão Válido
        criarReserva("CONFIRMADA", 100.0f);
        PagamentoRequest request = criarRequest("4000111122223333");

        // Simula que o PagamentoService é quem lança a exceção de validação
        when(pagamentoRepository.findByReservaIdReserva("RES123")).thenReturn(null);
        when(reservaService.buscarPorId("RES123")).thenThrow(new ValidacaoException("A reserva já foi processada ou está em status CONFIRMADA"));

        // Ação e Verificação
        assertThrows(ValidacaoException.class, () -> 
            pagamentoService.processarPagamento("RES123", request)
        );
        
        // Verifica se o serviço de confirmação nunca foi chamado
        verify(reservaService, never()).confirmarReserva(anyString());
        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }

    @Test
    void processarPagamento_DeveFalhar_QuandoReservaNaoExiste() {
        // Cenário: ID de Reserva Inexistente
        when(reservaService.buscarPorId("RES999")).thenThrow(new EntityNotFoundException("Reserva não encontrada."));
        PagamentoRequest request = criarRequest("4000111122223333");
        request.setIdReserva("RES999"); // Atualiza a request para o ID inexistente

        // Ação e Verificação
        assertThrows(EntityNotFoundException.class, () -> 
            pagamentoService.processarPagamento("RES999", request)
        );
        
        // Confirma que nenhuma lógica de pagamento foi executada
        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }
}