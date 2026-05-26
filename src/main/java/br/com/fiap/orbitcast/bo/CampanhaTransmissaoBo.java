package br.com.fiap.orbitcast.bo;

import br.com.fiap.orbitcast.dao.CampanhaTransmissaoDao;
import br.com.fiap.orbitcast.dao.CanalDao;
import br.com.fiap.orbitcast.dao.ClienteDao;
import br.com.fiap.orbitcast.dao.RegiaoDao;
import br.com.fiap.orbitcast.entities.CampanhaRegiao;
import br.com.fiap.orbitcast.entities.CampanhaTransmissao;
import br.com.fiap.orbitcast.entities.Regiao;
import br.com.fiap.orbitcast.exceptions.BusinessException;
import br.com.fiap.orbitcast.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class CampanhaTransmissaoBo {

    private static final Set<String> QUALIDADES = Set.of("SD", "HD", "FULL_HD", "4K");
    private static final Set<String> STATUS = Set.of("PLANEJADA", "EM_ANALISE", "APROVADA", "CANCELADA", "FINALIZADA");

    @Inject
    CampanhaTransmissaoDao campanhaDao;

    @Inject
    ClienteDao clienteDao;

    @Inject
    CanalDao canalDao;

    @Inject
    RegiaoDao regiaoDao;

    public List<CampanhaTransmissao> listar() {
        return campanhaDao.listar();
    }

    public CampanhaTransmissao buscarPorId(Long id) {
        return campanhaDao.buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Campanha nao encontrada."));
    }

    public List<Regiao> listarRegioes(Long campanhaId) {
        garantirCampanha(campanhaId);
        return campanhaDao.listarRegioes(campanhaId);
    }

    public CampanhaTransmissao cadastrar(CampanhaTransmissao campanha) {
        validar(campanha);
        return campanhaDao.inserir(campanha);
    }

    public CampanhaTransmissao atualizar(Long id, CampanhaTransmissao campanha) {
        validar(campanha);
        if (!campanhaDao.atualizar(id, campanha)) {
            throw new EntityNotFoundException("Campanha nao encontrada.");
        }
        return buscarPorId(id);
    }

    public void remover(Long id) {
        if (!campanhaDao.remover(id)) {
            throw new EntityNotFoundException("Campanha nao encontrada.");
        }
    }

    public void adicionarRegiao(Long campanhaId, Long regiaoId, CampanhaRegiao request) {
        garantirCampanha(campanhaId);
        if (!regiaoDao.existe(regiaoId)) {
            throw new BusinessException("Regiao informada nao existe.");
        }
        if (campanhaDao.campanhaPossuiRegiao(campanhaId, regiaoId)) {
            throw new BusinessException("Regiao ja esta associada a campanha.");
        }

        int prioridade = request == null || request.getPrioridade() == null ? 3 : request.getPrioridade();
        if (prioridade < 1 || prioridade > 5) {
            throw new BusinessException("Prioridade da regiao na campanha deve ficar entre 1 e 5.");
        }

        String observacao = request == null ? null : request.getObservacao();
        campanhaDao.adicionarRegiao(new CampanhaRegiao(campanhaId, regiaoId, prioridade, observacao));
    }

    public void removerRegiao(Long campanhaId, Long regiaoId) {
        garantirCampanha(campanhaId);
        if (!campanhaDao.removerRegiao(campanhaId, regiaoId)) {
            throw new EntityNotFoundException("Associacao entre campanha e regiao nao encontrada.");
        }
    }

    private void validar(CampanhaTransmissao campanha) {
        if (campanha == null) {
            throw new BusinessException("Campanha deve ser informada.");
        }
        if (campanha.getClienteId() == null || !clienteDao.existe(campanha.getClienteId())) {
            throw new BusinessException("Cliente informado para a campanha nao existe.");
        }
        if (campanha.getCanalId() == null || !canalDao.existe(campanha.getCanalId())) {
            throw new BusinessException("Canal informado para a campanha nao existe.");
        }
        if (isBlank(campanha.getNome())) {
            throw new BusinessException("Nome da campanha e obrigatorio.");
        }
        if (campanha.getDataInicio() == null || campanha.getDataFim() == null) {
            throw new BusinessException("Datas de inicio e fim sao obrigatorias.");
        }
        if (campanha.getDataInicio().isAfter(campanha.getDataFim())) {
            throw new BusinessException("Data de inicio nao pode ser posterior a data de fim.");
        }
        if (campanha.getDuracaoHoras() == null || campanha.getDuracaoHoras() <= 0) {
            throw new BusinessException("Duracao em horas deve ser maior que zero.");
        }
        campanha.setQualidadeDesejada(normalizarQualidade(campanha.getQualidadeDesejada()));
        campanha.setStatus(normalizarStatus(campanha.getStatus()));
        if (campanha.getOrcamento() == null || campanha.getOrcamento().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Orcamento deve ser maior ou igual a zero.");
        }
    }

    private void garantirCampanha(Long campanhaId) {
        if (campanhaId == null || !campanhaDao.existe(campanhaId)) {
            throw new EntityNotFoundException("Campanha nao encontrada.");
        }
    }

    private String normalizarQualidade(String qualidade) {
        if (isBlank(qualidade)) {
            throw new BusinessException("Qualidade desejada e obrigatoria.");
        }
        String valor = qualidade.strip().toUpperCase();
        if (!QUALIDADES.contains(valor)) {
            throw new BusinessException("Qualidade desejada deve ser SD, HD, FULL_HD ou 4K.");
        }
        return valor;
    }

    private String normalizarStatus(String status) {
        if (isBlank(status)) {
            return "PLANEJADA";
        }
        String valor = status.strip().toUpperCase();
        if (!STATUS.contains(valor)) {
            throw new BusinessException("Status da campanha invalido.");
        }
        return valor;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
