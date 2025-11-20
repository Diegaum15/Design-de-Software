package com.seucantinho.service;

import com.seucantinho.model.Cliente;
import com.seucantinho.repository.ClienteRepository;
import com.seucantinho.exception.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioService usuarioService;
    // Futuro: private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, UsuarioService usuarioService) {
        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Registra um novo Cliente no sistema.
     * @param cliente O objeto Cliente a ser salvo.
     * @return O Cliente registrado.
     */
    public Cliente registrarCliente(Cliente cliente) {
        // 1. Validação de Unicidade de Email (herdada do UsuarioService)
        usuarioService.verificarEmailUnico(cliente.getEmail());

        // 2. Validação de CPF
        if (cliente.getCpf() == null || cliente.getCpf().length() != 11) {
            throw new ValidacaoException("CPF inválido ou não fornecido.");
        }
        verificarCpfUnico(cliente.getCpf());

        // 3. (Futuro) Codificar a senha antes de salvar
        // cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));

        return clienteRepository.save(cliente);
    }

    /**
     * Busca um Cliente pelo ID.
     * @param idCliente O ID do cliente.
     * @return O Cliente encontrado.
     * @throws EntityNotFoundException se o cliente não for encontrado.
     */
    public Cliente buscarPorId(String idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + idCliente + " não encontrado."));
    }

    /**
     * Busca um Cliente pelo CPF.
     * @param cpf O CPF do cliente.
     * @return O Cliente encontrado.
     * @throws EntityNotFoundException se o cliente não for encontrado.
     */
    public Cliente buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente com CPF " + cpf + " não encontrado.");
        }
        return cliente;
    }

    /**
     * Lista todos os Clientes.
     * @return Uma lista de Clientes.
     */
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    /**
     * Atualiza os dados de um Cliente.
     * @param cliente Cliente com os dados atualizados.
     * @return Cliente atualizado.
     */
    public Cliente atualizarCliente(Cliente cliente) {
        // Garante que o cliente existe antes de salvar as alterações
        buscarPorId(cliente.getIdUsuario()); 
        
        // Regra: Não permitir a alteração do CPF ou Email se já estiverem em uso por outro usuário.
        // Validação Futura: Se a senha for alterada, ela deve ser codificada.
        
        return clienteRepository.save(cliente);
    }

    /**
     * Verifica se um CPF já está em uso por outro cliente.
     * @param cpf CPF a ser verificado.
     * @throws ValidacaoException se o CPF já estiver cadastrado.
     */
    private void verificarCpfUnico(String cpf) {
        if (clienteRepository.findByCpf(cpf) != null) {
            throw new ValidacaoException("O CPF " + cpf + " já está cadastrado no sistema.");
        }
    }
}