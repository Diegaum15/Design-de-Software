package com.seucantinho.service;

import com.seucantinho.model.Cliente;
import com.seucantinho.repository.ClienteRepository;
import com.seucantinho.dto.ClienteDTO; 
import com.seucantinho.model.Usuario;
import com.seucantinho.exception.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioService usuarioService;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, UsuarioService usuarioService) {
        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Converte ClienteDTO para Entidade Cliente.
     */
    private Cliente toEntity(ClienteDTO dto) {
    // Usamos o SuperBuilder para construir o Cliente que herda de Usuario
        return Cliente.builder()
                .idUsuario(dto.getIdUsuario()) // Opcional, será nulo no registro
                .nome(dto.getNome())
                .email(dto.getEmail())
                .cpf(dto.getCpf())
                .senha(dto.getSenha())
                .telefone(dto.getTelefone())
                .endereco(dto.getEndereco())
                .build();
    }

    /**
     * Converte Entidade Cliente para ClienteDTO (ocultando a senha).
     */
    public ClienteDTO toDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setIdUsuario(cliente.getIdUsuario());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setCpf(cliente.getCpf());
        dto.setTelefone(cliente.getTelefone());
        dto.setEndereco(cliente.getEndereco());
        // IMPORTANTE: NUNCA RETORNAR A SENHA NO DTO DE SAÍDA!
        
        return dto;
    }

    /**
     * Registra um novo Cliente no sistema.
     * @param clienteDTO O DTO Cliente a ser salvo.
     * @return O Cliente registrado em formato DTO.
     */
    public ClienteDTO registrarCliente(ClienteDTO clienteDTO) {
        
        // 1. CONVERTE DTO -> ENTITY
        Cliente cliente = toEntity(clienteDTO); 

        // 2. Validação e Lógica de Negócio
        usuarioService.verificarEmailUnico(cliente.getEmail());
        
        if (cliente.getCpf() == null || cliente.getCpf().length() < 11) {
            throw new ValidacaoException("CPF inválido ou não fornecido.");
        }
        verificarCpfUnico(cliente.getCpf()); 

        // 3. Salva a Entity
        // Futuro: Codificar a senha aqui antes de salvar
        Cliente novoCliente = clienteRepository.save(cliente);

        // 4. CONVERTE ENTITY -> DTO E RETORNA
        return toDTO(novoCliente);
    }

    /**
     * Busca um Cliente pelo ID e retorna em formato DTO.
     * @param idCliente O ID do cliente.
     * @return O Cliente encontrado.
     * @throws EntityNotFoundException se o cliente não for encontrado.
     */
    public ClienteDTO buscarPorId(String idCliente) {
        // Busca a Entity
        Cliente cliente = buscarEntityPorId(idCliente); 
        
        // Converte Entity -> DTO e retorna
        return toDTO(cliente);
    }

    /**
     * Busca um Cliente pelo ID e retorna a Entidade.
     * Útil para métodos internos ou serviços que precisam da Entity.
     */
    public Cliente buscarEntityPorId(String idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente com ID " + idCliente + " não encontrado."));
    }

    /**
     * Busca um Cliente pelo CPF.
     * @param cpf O CPF do cliente.
     * @return O Cliente encontrado em DTO.
     * @throws EntityNotFoundException se o cliente não for encontrado.
     */
    public ClienteDTO buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf);
        if (cliente == null) {
            throw new EntityNotFoundException("Cliente com CPF " + cpf + " não encontrado.");
        }
        return toDTO(cliente);
    }

    // Método para validar se o CPF é único
    private void verificarCpfUnico(String cpf) {
        if (clienteRepository.findByCpf(cpf) != null) {
            throw new ValidacaoException("O CPF " + cpf + " já está cadastrado no sistema.");
        }
    }
    
    // Novo método para verificar email único no contexto de atualização
    private void verificarEmailUnicoAtualizacao(String novoEmail, String idAtual) {
        Usuario usuarioComNovoEmail = usuarioService.buscarPorEmail(novoEmail);
        
        // Se achou um usuário E o ID dele não é o ID que estamos atualizando
        if (usuarioComNovoEmail != null && !idAtual.equals(usuarioComNovoEmail.getIdUsuario())) {
            throw new ValidacaoException("O email " + novoEmail + " já está em uso por outro usuário.");
        }
    }

    /**
     * Atualiza os dados de um Cliente existente.
     * @param id O ID do cliente a ser atualizado.
     * @param clienteDetalhesDTO DTO com os dados a serem alterados.
     * @return Cliente atualizado em DTO.
     */
    @Transactional
    public ClienteDTO atualizarCliente(String id, ClienteDTO clienteDetalhesDTO) {
        Cliente clienteExistente = buscarEntityPorId(id);

        // 1. Validação de Email (se o email mudou, checa unicidade)
        if (!clienteExistente.getEmail().equals(clienteDetalhesDTO.getEmail())) {
            verificarEmailUnicoAtualizacao(clienteDetalhesDTO.getEmail(), id);
            clienteExistente.setEmail(clienteDetalhesDTO.getEmail());
        }

        // 2. Bloquear alteração de CPF (Regra de Negócio)
        if (!clienteExistente.getCpf().equals(clienteDetalhesDTO.getCpf())) {
            throw new ValidacaoException("A alteração do CPF (" + clienteExistente.getCpf() + ") não é permitida após o registro.");
        }

        // 3. Atualizar campos permitidos
        clienteExistente.setNome(clienteDetalhesDTO.getNome());
        clienteExistente.setTelefone(clienteDetalhesDTO.getTelefone());
        clienteExistente.setEndereco(clienteDetalhesDTO.getEndereco());
        
        // Atualização de Senha
        if (clienteDetalhesDTO.getSenha() != null && !clienteDetalhesDTO.getSenha().isEmpty()) {
            clienteExistente.setSenha(clienteDetalhesDTO.getSenha()); // Futuro: Codificar a senha
        }

        // Persiste e converte Entity -> DTO
        return toDTO(clienteRepository.save(clienteExistente));
    }
        
    /**
     * Deleta um Cliente pelo ID.
     * @param id O ID do cliente a ser deletado.
     * @throws EntityNotFoundException se o cliente não for encontrado.
     */
    @Transactional
    public void deletarCliente(String id) {
        Cliente cliente = buscarEntityPorId(id); 
        clienteRepository.delete(cliente);
    }

    /**
     * Lista todos os Clientes.
     * @return Uma lista de todos os Clientes em DTO.
     */
    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::toDTO) // Mapeia cada Entity para um DTO
                .toList();
    }
}