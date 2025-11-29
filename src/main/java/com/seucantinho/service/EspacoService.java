package com.seucantinho.service;

import com.seucantinho.dto.EspacoDTO;
import com.seucantinho.model.*; // Importe todas as entidades, incluindo Salao, Chacara, QuadraEsportiva, Reserva
import com.seucantinho.repository.EspacoRepository;
import com.seucantinho.repository.ReservaRepository;
import com.seucantinho.exception.ValidacaoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EspacoService {

    private final EspacoRepository espacoRepository;
    private final ReservaRepository reservaRepository;
    private final FilialService filialService; // Assumindo a existência de um FilialService

    @Autowired
    public EspacoService(EspacoRepository espacoRepository, ReservaRepository reservaRepository, FilialService filialService) {
        this.espacoRepository = espacoRepository;
        this.reservaRepository = reservaRepository;
        // Mock de FilialService: Na implementação real, você injetaria o serviço de Filial
        this.filialService = filialService; 
    }
    
    // ------------------------------------------------------------------------
    // CONVERSÃO DTO <-> ENTITY (MÉTODO AUXILIAR)
    // ------------------------------------------------------------------------
    
    // Converte a ENTITY (Salao, Chacara, etc.) para o DTO genérico
    public EspacoDTO toDTO(Espaco espaco) {
        EspacoDTO dto = new EspacoDTO();
        
        // Campos Comuns
        dto.setIdEspaco(espaco.getIdEspaco());
        dto.setNome(espaco.getNome());
        dto.setTipo(espaco.getTipo());
        dto.setCapacidade(espaco.getCapacidade());
        dto.setPreco(espaco.getPreco());
        dto.setFoto(espaco.getFoto());
        // Relacionamento N:1 com Filial
        if (espaco.getFilial() != null) {
            dto.setIdFilial(espaco.getFilial().getIdFilial());
        }

        // Campos Específicos (Downcasting e Mapeamento)
        if (espaco instanceof Salao salao) {
            dto.setTamanhoCozinha(salao.getTamanhoCozinha());
            dto.setQuantidadeCadeiras(salao.getQuantidadeCadeiras());
            dto.setAreaTotal(salao.getAreaTotal());
        } else if (espaco instanceof Chacara chacara) {
            dto.setTemPiscina(chacara.getTemPiscina());
            dto.setNumQuartos(chacara.getNumQuartos());
            dto.setAreaLazer(chacara.getAreaLazer());
            dto.setEstacionamentoCapacidade(chacara.getEstacionamentoCapacidade());
        } else if (espaco instanceof QuadraEsportiva quadra) {
            dto.setTipoPiso(quadra.getTipoPiso());
            dto.setTipoEsportes(quadra.getTipoEsportes());
        }
        
        return dto;
    }

    // Converte o DTO genérico para a ENTITY específica (Salao, Chacara, etc.)
    private Espaco convertDtoToModel(EspacoDTO dto) {
        
        // Validação básica do tipo
        if (dto.getTipo() == null || dto.getTipo().trim().isEmpty()) {
            throw new ValidacaoException("O tipo de espaço é obrigatório.");
        }
        
        // Instancia a Entidade correta
        Espaco espaco;
        switch (dto.getTipo().toUpperCase()) {
            case "SALAO":
                // Utilizando SuperBuilder para garantir o mapeamento de campos herdados
                Salao.SalaoBuilder<?, ?> salaoBuilder = Salao.builder()
                        .tamanhoCozinha(dto.getTamanhoCozinha())
                        .quantidadeCadeiras(dto.getQuantidadeCadeiras() != null ? dto.getQuantidadeCadeiras() : 0)
                        .areaTotal(dto.getAreaTotal() != null ? dto.getAreaTotal() : 0.0f);
                espaco = salaoBuilder.build();
                break;
            case "CHACARA":
                Chacara.ChacaraBuilder<?, ?> chacaraBuilder = Chacara.builder()
                        .temPiscina(dto.getTemPiscina() != null ? dto.getTemPiscina() : false)
                        .numQuartos(dto.getNumQuartos() != null ? dto.getNumQuartos() : 0)
                        .areaLazer(dto.getAreaLazer())
                        .estacionamentoCapacidade(dto.getEstacionamentoCapacidade() != null ? dto.getEstacionamentoCapacidade() : 0);
                espaco = chacaraBuilder.build();
                break;
            case "QUADRAESPORTIVA":
                QuadraEsportiva.QuadraEsportivaBuilder<?, ?> quadraBuilder = QuadraEsportiva.builder()
                        .tipoPiso(dto.getTipoPiso())
                        .tipoEsportes(dto.getTipoEsportes());
                espaco = quadraBuilder.build();
                break;
            default:
                throw new ValidacaoException("Tipo de espaço inválido: " + dto.getTipo());
        }

        // Mapeamento dos Campos Comuns
        espaco.setIdEspaco(dto.getIdEspaco()); 
        espaco.setNome(dto.getNome());
        espaco.setTipo(dto.getTipo());
        espaco.setCapacidade(dto.getCapacidade());
        espaco.setPreco(dto.getPreco());
        espaco.setFoto(dto.getFoto());

        // Busca e Associa a Entidade Filial gerenciada
        // A FilialService deve existir e ter um método buscarPorId
        Filial filial = filialService.buscarPorId(dto.getIdFilial()); 
        espaco.setFilial(filial); 
        
        return espaco;
    }
    
    // ------------------------------------------------------------------------
    // CRUD IMPLEMENTADO COM DTO
    // ------------------------------------------------------------------------

    @Transactional
    public EspacoDTO salvarEspaco(EspacoDTO espacoDTO) {
        
        // 1. CONVERTE DTO para ENTITY
        Espaco espaco = convertDtoToModel(espacoDTO); 

        // 2. Validação de Negócio 
        if (espaco.getNome() == null || espaco.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome do espaço não pode ser vazio.");
        }
        
        // 3. Persiste a Entity
        Espaco espacoSalvo = espacoRepository.save(espaco);
        
        // 4. CONVERTE ENTITY para DTO e retorna
        return toDTO(espacoSalvo);
    }
    
    // BUSCA POR ID
    public EspacoDTO buscarPorId(String idEspaco) {
        Espaco espaco = buscarEntityPorId(idEspaco);
        return toDTO(espaco);
    }
    
    // MÉTODO AUXILIAR INTERNO para buscar a Entidade real
    public Espaco buscarEntityPorId(String idEspaco) {
        return espacoRepository.findById(idEspaco)
                .orElseThrow(() -> new EntityNotFoundException("Espaço com ID " + idEspaco + " não encontrado."));
    }
    
    // LISTAR TODOS
    public List<EspacoDTO> listarTodos() {
        return espacoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // LISTAR DISPONÍVEIS
    public List<EspacoDTO> listarDisponiveis(LocalDateTime dataInicio, LocalDateTime dataFim, String tipo) {
        
        // 1. Busca todos os espaços no repositório
        List<Espaco> todosEspacos = espacoRepository.findAll();

        // 2. Filtra por tipo, se fornecido
        if (tipo != null && !tipo.trim().isEmpty()) {
            todosEspacos = todosEspacos.stream()
                .filter(e -> e.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
        }
        
        // 3. Filtra por Disponibilidade e converte para DTO
        return todosEspacos.stream()
                // A chave é a chamada ao método de verificação de sobreposição
                .filter(espaco -> verificarDisponibilidade(espaco.getIdEspaco(), dataInicio, dataFim)) 
                .map(this::toDTO) 
                .collect(Collectors.toList());
    }

    // DELETAR 
    @Transactional
    public void deletarEspaco(String idEspaco) {
        Espaco espaco = buscarEntityPorId(idEspaco);
        
        // REGRA DE NEGÓCIO: Não pode deletar um espaço com reservas CONFIRMADAS
        List<Reserva> reservasAtivas = reservaRepository.findSobreposicaoDeReserva(idEspaco, LocalDateTime.MIN, LocalDateTime.MAX);
        
        // O método findSobreposicaoDeReserva foi projetado para datas, mas pode ser 
        // reutilizado para verificar qualquer reserva confirmada se usarmos MIN/MAX.
        // Se houver reservas ativas, o sistema deve impedir a exclusão.
        if (!reservasAtivas.isEmpty()) {
             throw new ValidacaoException("Não é possível excluir o espaço, pois ele possui reservas ativas ou futuras confirmadas.");
        }
        
        espacoRepository.delete(espaco);
    }
    
    /**
     * Lógica crítica: Verifica se não há sobreposição de reservas CONFIRMADAS.
     * @param idEspaco ID do espaço.
     * @param dataInicio Início da janela de checagem.
     * @param dataFim Fim da janela de checagem.
     * @return true se o espaço estiver disponível, false caso haja sobreposição.
     */
    public boolean verificarDisponibilidade(String idEspaco, LocalDateTime dataInicio, LocalDateTime dataFim) {
        
        // Validação básica de datas
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim) || dataInicio.isEqual(dataFim)) {
            // Em um sistema real, essa validação deveria ocorrer antes da chamada.
            throw new ValidacaoException("Período de reserva inválido.");
        }
        
        // O ReservaRepository faz o trabalho pesado consultando no banco.
        List<Reserva> reservasConflitantes = reservaRepository.findSobreposicaoDeReserva(
            idEspaco, dataInicio, dataFim
        );
        
        // Retorna true se a lista de conflitos estiver vazia, ou seja, está disponível.
        return reservasConflitantes.isEmpty(); 
    }
}