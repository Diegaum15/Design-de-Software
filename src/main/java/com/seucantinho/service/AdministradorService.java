package com.seucantinho.service;

import com.seucantinho.model.Administrador;
import com.seucantinho.model.Filial;
import com.seucantinho.repository.AdministradorRepository;
import com.seucantinho.exception.UsuarioNaoEncontradoException;
import com.seucantinho.exception.ValidacaoException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final UsuarioService usuarioService;
    private final FilialService filialService;  

    @Autowired
    public AdministradorService(
            AdministradorRepository administradorRepository,
            UsuarioService usuarioService,
            FilialService filialService   
    ) {
        this.administradorRepository = administradorRepository;
        this.usuarioService = usuarioService;
        this.filialService = filialService;      
    }

    public Administrador buscarPorMatricula(String matricula) {
        Administrador admin = administradorRepository.findByMatricula(matricula);
        if (admin == null) {
            throw new UsuarioNaoEncontradoException(
                "Administrador com matrícula " + matricula + " não encontrado."
            );
        }
        return admin;
    }

    public Administrador registrarAdministrador(Administrador administrador) {
        usuarioService.verificarEmailUnico(administrador.getEmail());
        return administradorRepository.save(administrador);
    }

    public List<Administrador> listarTodos() {
        return administradorRepository.findAll();
    }

    public Administrador buscarPorId(String id) {
        return administradorRepository.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException("Administrador com ID " + id + " não encontrado."));
    }

    // ---------------------------------------------------
    // FILIAIS GERENCIADAS
    // ---------------------------------------------------

    @Transactional
    public Administrador adicionarFilialGerenciada(String idAdmin, String idFilial) {
        Administrador admin = buscarPorId(idAdmin);
        Filial filial = filialService.buscarPorId(idFilial);

        admin.getFiliaisGerenciadas().add(filial);

        return administradorRepository.save(admin);
    }

    @Transactional
    public Administrador removerFilialGerenciada(String idAdmin, String idFilial) {
        Administrador admin = buscarPorId(idAdmin);
        Filial filial = filialService.buscarPorId(idFilial);

        admin.getFiliaisGerenciadas().remove(filial);

        return administradorRepository.save(admin);
    }
}
