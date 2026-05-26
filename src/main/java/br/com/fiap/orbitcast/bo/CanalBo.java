package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.CanalDao;
import br.com.fiap.orbitcast.dao.ClienteDao;
import br.com.fiap.orbitcast.entities.Canal;
import br.com.fiap.orbitcast.exceptions.BusinessException;
import br.com.fiap.orbitcast.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CanalBo {

    @Inject
    CanalDao canalDao;

    @Inject
    ClienteDao clienteDao;

    public List<Canal> listar() {
        return canalDao.listar();
    }

    public Canal buscarPorId(Long id) {
        return canalDao.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Canal nao encontrado."));
    }

    public Canal cadastrar(Canal canal) {
        validar(canal);
        return canalDao.inserir(canal);
    }

    public Canal atualizar(Long id, Canal canal) {
        validar(canal);
        if (!canalDao.atualizar(id, canal)) {
            throw new EntityNotFoundException("Canal nao encontrado.");
        }
        return buscarPorId(id);
    }

    public void remover(Long id) {
        if (!canalDao.remover(id)) {
            throw new EntityNotFoundException("Canal nao encontrado.");
        }
    }

    private void validar(Canal canal) {
        if (canal == null) {
            throw new BusinessException("Canal deve ser informado.");
        }
        if (canal.getClienteId() == null || !clienteDao.existe(canal.getClienteId())) {
            throw new BusinessException("Cliente informado para o canal nao existe.");
        }
        if (isBlank(canal.getNome())) {
            throw new BusinessException("Nome do canal e obrigatorio.");
        }
        if (isBlank(canal.getTipoConteudo())) {
            throw new BusinessException("Tipo de conteudo e obrigatorio.");
        }
        if (isBlank(canal.getPublicoAlvo())) {
            throw new BusinessException("Publico-alvo e obrigatorio.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
