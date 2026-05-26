package br.com.fiap.orbitcast.dao;

import br.com.fiap.orbitcast.connection.DatabaseConnection;
import br.com.fiap.orbitcast.dto.DashboardResumo;
import br.com.fiap.orbitcast.exceptions.DataAccessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class DashboardDao {

    @Inject
    DatabaseConnection databaseConnection;

    public DashboardResumo resumo() {
        try (Connection connection = databaseConnection.getConnection()) {
            DashboardResumo resumo = new DashboardResumo();
            resumo.setTotalClientes(contar(connection, "clientes"));
            resumo.setTotalCanais(contar(connection, "canais"));
            resumo.setTotalRegioes(contar(connection, "regioes"));
            resumo.setTotalCampanhas(contar(connection, "campanhas_transmissao"));
            resumo.setTotalSimulacoes(contar(connection, "simulacoes"));
            resumo.setCampanhasPorStatus(agrupar(connection, "campanhas_transmissao", "status"));
            resumo.setSimulacoesPorViabilidade(agrupar(connection, "simulacoes", "viabilidade"));
            preencherMetricasSimulacao(connection, resumo);
            return resumo;
        } catch (Exception exception) {
            throw new DataAccessException("Erro ao carregar resumo do dashboard.", exception);
        }
    }

    private int contar(Connection connection, String tabela) throws Exception {
        String sql = "SELECT COUNT(*) total FROM " + tabela;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt("total");
        }
    }

    private Map<String, Integer> agrupar(Connection connection, String tabela, String coluna) throws Exception {
        String sql = "SELECT " + coluna + " nome, COUNT(*) total FROM " + tabela + " GROUP BY " + coluna + " ORDER BY " + coluna;
        Map<String, Integer> dados = new LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                dados.put(resultSet.getString("nome"), resultSet.getInt("total"));
            }
        }

        return dados;
    }

    private void preencherMetricasSimulacao(Connection connection, DashboardResumo resumo) throws Exception {
        String sql = """
                SELECT COALESCE(SUM(alcance_estimado), 0) alcance_total,
                       COALESCE(AVG(custo_estimado), 0) custo_medio,
                       COALESCE(AVG(qualidade_sinal), 0) qualidade_media
                  FROM simulacoes
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            resumo.setAlcanceEstimadoTotal(resultSet.getInt("alcance_total"));
            resumo.setCustoMedioSimulacoes(resultSet.getBigDecimal("custo_medio"));
            resumo.setQualidadeMediaSinal(resultSet.getBigDecimal("qualidade_media"));
        }

        if (resumo.getCustoMedioSimulacoes() == null) {
            resumo.setCustoMedioSimulacoes(BigDecimal.ZERO);
        }
        if (resumo.getQualidadeMediaSinal() == null) {
            resumo.setQualidadeMediaSinal(BigDecimal.ZERO);
        }
    }
}
