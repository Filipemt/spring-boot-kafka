package com.filipecode.icompras.clientes.service;

import com.filipecode.icompras.clientes.controller.dto.ClientePatchRequest;
import com.filipecode.icompras.clientes.controller.dto.ClienteRequest;
import com.filipecode.icompras.clientes.controller.dto.ClienteResponse;
import com.filipecode.icompras.clientes.controller.dto.FiltroCliente;
import com.filipecode.icompras.clientes.exception.ClienteJaExistenteException;
import com.filipecode.icompras.clientes.exception.ClienteNaoEncontradoException;
import com.filipecode.icompras.clientes.mapper.ClienteMapper;
import com.filipecode.icompras.clientes.model.Cliente;
import com.filipecode.icompras.clientes.model.valueObject.Cpf;
import com.filipecode.icompras.clientes.repository.ClienteRepository;
import com.filipecode.icompras.clientes.specification.ClienteSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    public ClienteResponse salvar(ClienteRequest request) {
        Cliente cliente = clienteMapper.toEntity(request);
        validarCpfUnico(cliente.getCpf());
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    public ClienteResponse obterPorCodigo(Long codigo) {
        return clienteMapper.toResponse(buscarCliente(codigo));
    }

    public Page<ClienteResponse> listar(FiltroCliente filtro, Pageable pageable) {
        return clienteRepository.findAll(ClienteSpecification.comFiltros(filtro), pageable)
                .map(clienteMapper::toResponse);
    }

    @Transactional
    public ClienteResponse atualizar(Long codigo, ClienteRequest request) {
        Cliente cliente = buscarCliente(codigo);
        Cpf cpf = Cpf.of(request.cpf());
        validarCpfUnicoSeAlterado(cliente, cpf);

        cliente.atualizarDados(request.nome(), cpf, request.logradouro(), request.numero(),
                request.bairro(), request.email(), request.telefone());

        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteResponse atualizarParcial(Long codigo, ClientePatchRequest request) {
        Cliente cliente = buscarCliente(codigo);
        Cpf cpf = Cpf.of(request.cpf());
        validarCpfUnicoSeAlterado(cliente, cpf);

        cliente.atualizarDadosParciais(request.nome(), cpf, request.logradouro(), request.numero(),
                request.bairro(), request.email(), request.telefone());

        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void deletar(Long codigo) {
        Cliente cliente = buscarCliente(codigo);
        cliente.desativar();
        clienteRepository.save(cliente);
    }

    private Cliente buscarCliente(Long codigo) {
        return clienteRepository.findByCodigoAndAtivoTrue(codigo)
                .orElseThrow(() -> new ClienteNaoEncontradoException(codigo));
    }

    private void validarCpfUnico(Cpf cpf) {
        if (clienteRepository.existsByCpf(cpf)) {
            throw new ClienteJaExistenteException(cpf.valor());
        }
    }

    private void validarCpfUnicoSeAlterado(Cliente cliente, Cpf cpf) {
        if (cpf != null && !cpf.equals(cliente.getCpf())) {
            validarCpfUnico(cpf);
        }
    }
}
