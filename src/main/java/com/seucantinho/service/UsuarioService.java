package com.seucantinho.service;

import com.seucantinho.model.Usuario;
import com.seucantinho.repository.UsuarioRepository;
import com.seucantinho.exception.UsuarioNaoEncontradoException;
import com.seucantinho.exception.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Importação que será necessária no futuro para segurança:
// import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    // private final PasswordEncoder passwordEncoder; // Deixe comentado por enquanto

    @Autowired
    public UsuarioService(
        UsuarioRepository usuarioRepository
        /*, PasswordEncoder passwordEncoder // Deixe comentado por enquanto */
    ) {
        this.usuarioRepository = usuarioRepository;
        // this.passwordEncoder = passwordEncoder; // Deixe comentado por enquanto
    }

    /**
     * Busca um usuário pelo ID.
     * @param idUsuario O ID do usuário.
     * @return O objeto Usuario.
     * @throws UsuarioNaoEncontradoException se o usuário não for encontrado.
     */
    public Usuario buscarPorId(String idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário com ID " + idUsuario + " não encontrado."));
    }

    /**
     * Busca um usuário pelo Email (chave essencial para login).
     * @param email O email do usuário.
     * @return O objeto Usuario.
     * @throws UsuarioNaoEncontradoException se o usuário não for encontrado.
     */
    public Usuario buscarPorEmail(String email) {
        // CORREÇÃO: Usando findByEmail do Repository (que retorna Optional)
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário com email " + email + " não encontrado."));
    }

    /**
     * Valida se um email já está em uso (regra de unicidade).
     * @param email Email a ser verificado.
     * @throws ValidacaoException se o email já estiver cadastrado.
     */
    public void verificarEmailUnico(String email) {
        // CORREÇÃO: Usando existsByEmail do Repository (retorna boolean)
        if (usuarioRepository.existsByEmail(email)) {
            throw new ValidacaoException("O email " + email + " já está cadastrado no sistema.");
        }
    }

    /**
     * Método básico de salvar (será estendido por ClienteService e AdministradorService).
     * Usado para atualizações ou funções genéricas.
     * @param usuario O objeto Usuario a ser salvo.
     * @return O Usuario salvo.
     */
    public Usuario salvarUsuario(Usuario usuario) {
        // Validação Futura: Se for uma criação, a senha deve ser codificada aqui
        // Exemplo: usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }
}