package com.seucantinho.service;

import com.seucantinho.model.Filial;
import com.seucantinho.repository.FilialRepository;
import com.seucantinho.exception.ValidacaoException; // Usaremos para validações de domínio
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Marca a classe como um componente de serviço Spring
public class FilialService {

    private final FilialRepository filialRepository;

    // Injeção de dependência do Repositório via construtor (prática recomendada)
    @Autowired
    public FilialService(FilialRepository filialRepository) {
        this.filialRepository = filialRepository;
    }

    /**
     * Salva ou atualiza uma nova Filial.
     * @param filial A entidade Filial a ser salva.
     * @return A Filial salva.
     */
    public Filial salvarFilial(Filial filial) {
        // Exemplo de regra de negócio: garantir que o nome da filial não seja nulo
        if (filial.getNomeFilial() == null || filial.getNomeFilial().trim().isEmpty()) {
            throw new ValidacaoException("O nome da filial não pode ser vazio.");
        }
        
        // Regra futura: Verificar se já existe uma filial com o mesmo nome (evitar duplicidade)
        
        return filialRepository.save(filial);
    }

    /**
     * Busca uma Filial pelo ID.
     * @param idFilial O ID da filial.
     * @return A Filial encontrada.
     * @throws EntityNotFoundException Se a filial não for encontrada.
     */
    public Filial buscarPorId(String idFilial) {
        return filialRepository.findById(idFilial)
                .orElseThrow(() -> new EntityNotFoundException("Filial com ID " + idFilial + " não encontrada."));
    }

    /**
     * Lista todas as Filiais ativas.
     * @return Uma lista de Filiais.
     */
    public List<Filial> listarTodas() {
        // No futuro, podemos adicionar uma lógica para listar apenas filiais ativas, se necessário.
        return filialRepository.findAll();
    }

    /**
     * Desativa logicamente uma Filial.
     * @param idFilial O ID da filial a ser desativada.
     */
    public void desativarFilial(String idFilial) {
        Filial filial = buscarPorId(idFilial);
        
        // Exemplo de regra de negócio: Apenas pode desativar se não houver reservas pendentes
        // Regra Futura: if (reservaService.existemReservasPendentes(filial.getIdFilial())) { ... }
        
        filial.setStatus(false); // Define status para false (desativado)
        filialRepository.save(filial);
    }
}