package com.seucantinho.service;

import com.seucantinho.model.Espaco;
import com.seucantinho.model.Reserva;
import com.seucantinho.repository.EspacoRepository;
import com.seucantinho.repository.ReservaRepository;
import com.seucantinho.exception.ValidacaoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EspacoService {

    private final EspacoRepository espacoRepository;
    private final ReservaRepository reservaRepository;

    @Autowired
    public EspacoService(EspacoRepository espacoRepository, ReservaRepository reservaRepository) {
        this.espacoRepository = espacoRepository;
        this.reservaRepository = reservaRepository;
    }

    /**
     * Salva um novo Espaço (ou uma subclasse: Salao, Chacara, QuadraEsportiva).
     * @param espaco A entidade Espaco a ser salva.
     * @return O Espaço salvo.
     */
    public Espaco salvarEspaco(Espaco espaco) {
        if (espaco.getNome() == null || espaco.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome do espaço não pode ser vazio.");
        }
        // Validação futura: Garantir que a Filial associada exista.
        return espacoRepository.save(espaco);
    }

    /**
     * Busca um Espaço pelo ID.
     * @param idEspaco O ID do espaço.
     * @return O Espaço encontrado.
     * @throws EntityNotFoundException Se o espaço não for encontrado.
     */
    public Espaco buscarPorId(String idEspaco) {
        return espacoRepository.findById(idEspaco)
                .orElseThrow(() -> new EntityNotFoundException("Espaço com ID " + idEspaco + " não encontrado."));
    }

    /**
     * Lista todos os Espaços cadastrados.
     * @return Uma lista de Espaços.
     */
    public List<Espaco> listarTodos() {
        return espacoRepository.findAll();
    }


    public Espaco atualizarEspaco(String id, Espaco dados) {

        Espaco atual = buscarPorId(id);

        // Atualizar campos comuns
        atual.setNome(dados.getNome());
        atual.setTipo(dados.getTipo());
        atual.setCapacidade(dados.getCapacidade());
        atual.setPreco(dados.getPreco());
        atual.setFoto(dados.getFoto());
        atual.setFilial(dados.getFilial());

        // Atualizar campos específicos (subclasse)
        if (atual instanceof Salao s && dados instanceof Salao d) {
            s.setTamanhoCozinha(d.getTamanhoCozinha());
            s.setQuantidadeCadeiras(d.getQuantidadeCadeiras());
            s.setAreaTotal(d.getAreaTotal());
        }

        if (atual instanceof Chacara c && dados instanceof Chacara d) {
            c.setTemPiscina(d.getTemPiscina());
            c.setNumQuartos(d.getNumQuartos());
            c.setAreaLazer(d.getAreaLazer());
            c.setEstacionamentoCapacidade(d.getEstacionamentoCapacidade());
        }

        if (atual instanceof QuadraEsportiva q && dados instanceof QuadraEsportiva d) {
            q.setTipoPiso(d.getTipoPiso());
            q.setTipoEsportes(d.getTipoEsportes());
        }

        return espacoRepository.save(atual);
    }

    public void deletarEspaco(String idEspaco) {

        List<Reserva> futuras = reservaRepository.findReservasFuturasByEspacoId(idEspaco);

        if (!futuras.isEmpty()) {
            throw new ValidacaoException("Não é possível remover: existem reservas futuras para este espaço.");
        }

        espacoRepository.deleteById(idEspaco);
    }

    /**
     * Verifica se um determinado espaço está disponível para um intervalo de tempo.
     * Esta é a lógica base que será usada antes de criar uma Reserva.
     * @param idEspaco ID do Espaço a verificar.
     * @param dataInicio Início do evento.
     * @param dataFim Fim do evento.
     * @return true se estiver disponível, false caso contrário.
     */
    public boolean verificarDisponibilidade(String idEspaco, LocalDateTime dataInicio, LocalDateTime dataFim) {
        Espaco espaco = buscarPorId(idEspaco);
        
        // Usa o método de consulta customizado no ReservaRepository
        List<Reserva> conflitos = reservaRepository.findReservasConflitantes(espaco, dataInicio, dataFim);
        
        // Se a lista de conflitos estiver vazia, o espaço está disponível.
        return conflitos.isEmpty();
    }

    /**
     * Lista todos os espaços disponíveis para um determinado intervalo de tempo e, opcionalmente, tipo.
     * @param dataInicio Início do evento.
     * @param dataFim Fim do evento.
     * @param tipo (Opcional) Tipo de espaço a filtrar (Salao, Chacara, etc.).
     * @return Lista de Espaços disponíveis.
     */
    public List<Espaco> listarDisponiveis(LocalDateTime dataInicio, LocalDateTime dataFim, String tipo) {
        List<Espaco> todosEspacos = espacoRepository.findAll();

        // 1. Filtrar por Tipo, se especificado
        if (tipo != null && !tipo.trim().isEmpty()) {
            todosEspacos = todosEspacos.stream()
                .filter(e -> e.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
        }
        
        // 2. Filtrar por Disponibilidade
        return todosEspacos.stream()
                .filter(espaco -> verificarDisponibilidade(espaco.getIdEspaco(), dataInicio, dataFim))
                .collect(Collectors.toList());
    }
}