package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.RegiaoDao;
import br.com.fiap.orbitcast.entities.Regiao;
import br.com.fiap.orbitcast.exceptions.BusinessException;
import br.com.fiap.orbitcast.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class RegiaoBo {

    @Inject
    RegiaoDao regiaoDao;

    public List<Regiao> listar() {
        return regiaoDao.listar();
    }

    public Regiao buscarPorId(Long id) {
        return regiaoDao.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Regiao nao encontrada."));
    }

    public Regiao cadastrar(Regiao regiao) {
        validar(regiao);
        return regiaoDao.inserir(regiao);
    }

    public Regiao atualizar(Long id, Regiao regiao) {
        validar(regiao);
        if (!regiaoDao.atualizar(id, regiao)) {
            throw new EntityNotFoundException("Regiao nao encontrada.");
        }
        return buscarPorId(id);
    }

    public void remover(Long id) {
        if (!regiaoDao.remover(id)) {
            throw new EntityNotFoundException("Regiao nao encontrada.");
        }
    }

    private void validar(Regiao regiao) {
        if (regiao == null) {
            throw new BusinessException("Regiao deve ser informada.");
        }
        if (isBlank(regiao.getNome())) {
            throw new BusinessException("Nome da regiao e obrigatorio.");
        }
        if (regiao.getPopulacaoEstimada() == null || regiao.getPopulacaoEstimada() < 0) {
            throw new BusinessException("Populacao estimada deve ser maior ou igual a zero.");
        }
        if (regiao.getIndiceConectividade() == null
                || regiao.getIndiceConectividade().compareTo(BigDecimal.ZERO) < 0
                || regiao.getIndiceConectividade().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("Indice de conectividade deve ficar entre 0 e 100.");
        }
        if (regiao.getPrioridadeSocial() == null || regiao.getPrioridadeSocial() < 1 || regiao.getPrioridadeSocial() > 5) {
            throw new BusinessException("Prioridade social deve ficar entre 1 e 5.");
        }
        if (regiao.getAreaKm2() == null || regiao.getAreaKm2().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Area em km2 deve ser maior que zero.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
