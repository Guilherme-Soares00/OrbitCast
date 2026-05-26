package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.ClienteDao;
import br.com.fiap.orbitcast.entities.Cliente;
import br.com.fiap.orbitcast.exceptions.BusinessException;
import br.com.fiap.orbitcast.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ClienteBo {

    @Inject
    ClienteDao clienteDao;

    public List<Cliente> listar() {
        return clienteDao.listar();
    }

    public Cliente buscarPorId(Long id) {
        return clienteDao.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente nao encontrado."));
    }

    public Cliente cadastrar(Cliente cliente) {
        validar(cliente);
        return clienteDao.inserir(cliente);
    }

    public Cliente atualizar(Long id, Cliente cliente) {
        validar(cliente);
        if (!clienteDao.atualizar(id, cliente)) {
            throw new EntityNotFoundException("Cliente nao encontrado.");
        }
        return buscarPorId(id);
    }

    public void remover(Long id) {
        if (!clienteDao.remover(id)) {
            throw new EntityNotFoundException("Cliente nao encontrado.");
        }
    }

    private void validar(Cliente cliente) {
        if (cliente == null) {
            throw new BusinessException("Cliente deve ser informado.");
        }
        if (isBlank(cliente.getNome())) {
            throw new BusinessException("Nome do cliente e obrigatorio.");
        }
        if (isBlank(cliente.getEmail()) || !cliente.getEmail().contains("@")) {
            throw new BusinessException("Email do cliente e obrigatorio e deve ser valido.");
        }
        if (isBlank(cliente.getSegmento())) {
            throw new BusinessException("Segmento do cliente e obrigatorio.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
