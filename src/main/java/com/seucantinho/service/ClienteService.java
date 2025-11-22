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

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, UsuarioService usuarioService) {
        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
    }

    public Cliente registrarCliente(Cliente cliente) {

        // 1. Validação do email
        usuarioService.verificarEmailUnico(cliente.getUsuario().getEmail());

        // 2. Validar CPF formatado
        validarCpf(cliente.getCpf());

        // 3. Verificar se já existe
        verificarCpfUnico(cliente.getCpf());

        return clienteRepository.save(cliente);
    }

    

    public Cliente buscarPorId(String idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> 
                    new EntityNotFoundException("Cliente com ID " + idCliente + " não encontrado.")
                );
    }

    public Cliente buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() ->
                    new EntityNotFoundException("Cliente com CPF " + cpf + " não encontrado.")
                );
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente atualizarCliente(String id, Cliente dadosAtualizados) {

        // 1. Buscar cliente existente
        Cliente clienteExistente = buscarPorId(id);

        // 2. Validar CPF no formato
        validarCpf(dadosAtualizados.getCpf());

        // 3. Verificar unicidade do CPF (exceto o próprio cliente)
        verificarCpfUnicoAtualizacao(
                id,
                dadosAtualizados.getCpf()
        );

        // 4. Atualizar dados do usuário interno
        clienteExistente.getUsuario().setNome(dadosAtualizados.getUsuario().getNome());
        clienteExistente.getUsuario().setEmail(dadosAtualizados.getUsuario().getEmail());
        clienteExistente.getUsuario().setTelefone(dadosAtualizados.getUsuario().getTelefone());

        // 5. Atualizar dados do cliente
        clienteExistente.setCpf(dadosAtualizados.getCpf());
        clienteExistente.setEndereco(dadosAtualizados.getEndereco());

        return clienteRepository.save(clienteExistente);
    }

    public void deletarCliente(String id) {
        buscarPorId(id); // lança 404 se não existir
        clienteRepository.deleteById(id);
    }


    private void verificarCpfUnico(String cpf) {
        if (clienteRepository.existsByCpf(cpf)) {
            throw new ValidacaoException("O CPF " + cpf + " já está cadastrado no sistema.");
        }
    }

    private void verificarCpfUnicoAtualizacao(String id, String cpf) {
        clienteRepository.findByCpf(cpf)
                .ifPresent(c -> {
                    if (!c.getIdUsuario().equals(id)) {
                        throw new ValidacaoException(
                                "O CPF " + cpf + " já está sendo usado por outro cliente."
                        );
                    }
                });
    }

    private void validarCpf(String cpf) {
        if (cpf == null || cpf.length() != 14) {
            throw new ValidacaoException(
                "CPF inválido. Utilize o formato 000.000.000-00 (14 caracteres)."
            );
        }
    }
}
