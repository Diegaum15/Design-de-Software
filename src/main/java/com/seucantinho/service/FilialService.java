package com.seucantinho.service;

import com.seucantinho.model.Filial;
import com.seucantinho.repository.FilialRepository;
import com.seucantinho.exception.ValidacaoException;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilialService {

    private final FilialRepository filialRepository;

    @Autowired
    public FilialService(FilialRepository filialRepository) {
        this.filialRepository = filialRepository;
    }

    public Filial salvarFilial(Filial filial) {
        if (filial.getNomeFilial() == null || filial.getNomeFilial().trim().isEmpty()) {
            throw new ValidacaoException("O nome da filial não pode ser vazio.");
        }
        return filialRepository.save(filial);
    }

    public Filial buscarPorId(String idFilial) {
        return filialRepository.findById(idFilial)
                .orElseThrow(() ->
                    new EntityNotFoundException("Filial com ID " + idFilial + " não encontrada."));
    }

    public List<Filial> listarTodas() {
        return filialRepository.findAll();
    }

    public void desativarFilial(String idFilial) {
        Filial filial = buscarPorId(idFilial);
        filial.setStatus(false);
        filialRepository.save(filial);
    }
}
