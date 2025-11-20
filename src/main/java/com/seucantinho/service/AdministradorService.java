package com.seucantinho.service;

import com.seucantinho.model.Administrador;
import com.seucantinho.model.Filial;
import com.seucantinho.repository.AdministradorRepository;
import com.seucantinho.exception.ValidacaoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final UsuarioService usuarioService;
    private final FilialService filialService; // Necessário para gerenciar filiais associadas
    // Futuro: private final PasswordEncoder passwordEncoder;

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

    // ------------------------------------------------------------------------
    // MÉTODOS DE REGISTRO E BUSCA
    // ------------------------------------------------------------------------

    /**
     * Registra um novo Administrador.
     * @param administrador O objeto Administrador a ser salvo.
     * @return O Administrador registrado.
     */
    public Administrador registrarAdministrador(Administrador administrador) {
        // 1. Validação de Unicidade de Email (herdada do UsuarioService)
        usuarioService.verificarEmailUnico(administrador.getEmail());

        // 2. (Futuro) Codificar a senha antes de salvar
        // administrador.setSenha(passwordEncoder.encode(administrador.getSenha()));

        return administradorRepository.save(administrador);
    }

    /**
     * Busca um Administrador pelo ID.
     * @param idAdmin O ID do administrador.
     * @return O Administrador encontrado.
     * @throws EntityNotFoundException se o administrador não for encontrado.
     */
    public Administrador buscarPorId(String idAdmin) {
        return administradorRepository.findById(idAdmin)
                .orElseThrow(() -> new EntityNotFoundException("Administrador com ID " + idAdmin + " não encontrado."));
    }

    /**
     * Lista todos os Administradores.
     * @return Uma lista de Administradores.
     */
    public List<Administrador> listarTodos() {
        return administradorRepository.findAll();
    }

    // ------------------------------------------------------------------------
    // MÉTODOS DE GESTÃO DE FILIAL
    // ------------------------------------------------------------------------

    /**
     * Adiciona uma Filial à lista de filiais gerenciadas por um Administrador.
     * @param idAdmin ID do administrador.
     * @param idFilial ID da filial a ser adicionada.
     * @return O Administrador atualizado.
     */
    public Administrador adicionarFilialGerenciada(String idAdmin, String idFilial) {
        Administrador admin = buscarPorId(idAdmin);
        Filial filial = filialService.buscarPorId(idFilial); // Reutiliza a lógica de busca da Filial
        
        List<Filial> filiais = admin.getFiliaisGerenciadas();
        
        // Verifica se a filial já está na lista
        boolean filialJaExiste = filiais.stream()
                                        .anyMatch(f -> f.getIdFilial().equals(idFilial));
        
        if (filialJaExiste) {
            throw new ValidacaoException("O administrador já gerencia a filial com ID " + idFilial);
        }
        
        filiais.add(filial);
        admin.setFiliaisGerenciadas(filiais);
        
        return administradorRepository.save(admin);
    }
    
    /**
     * Remove uma Filial da lista de filiais gerenciadas por um Administrador.
     * @param idAdmin ID do administrador.
     * @param idFilial ID da filial a ser removida.
     * @return O Administrador atualizado.
     */
    public Administrador removerFilialGerenciada(String idAdmin, String idFilial) {
        Administrador admin = buscarPorId(idAdmin);
        
        boolean removed = admin.getFiliaisGerenciadas()
                                .removeIf(f -> f.getIdFilial().equals(idFilial));
                                
        if (!removed) {
            throw new ValidacaoException("O administrador não gerencia a filial com ID " + idFilial);
        }
        
        return administradorRepository.save(admin);
    }
}