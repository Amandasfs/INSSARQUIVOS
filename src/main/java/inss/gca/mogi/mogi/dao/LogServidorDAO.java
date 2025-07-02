package inss.gca.mogi.mogi.dao;

import inss.gca.mogi.mogi.config.DatabaseConfig;
import inss.gca.mogi.mogi.model.LogServidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Classe responsável pelas operações de persistência relacionadas à tabela de logs dos servidores.
 * Permite o registro de ações realizadas pelos usuários do sistema para fins de auditoria.
 */
public class LogServidorDAO {

    /**
     * Registra uma entrada de log no banco de dados.
     *
     * @param log Objeto LogServidor contendo os dados da ação a ser registrada.
     */
    public void registrarLog(LogServidor log) {
        String sql = "INSERT INTO log_servidor (servidor_id, acao, data_hora, detalhes) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, log.getServidorId());
            stmt.setString(2, log.getAcao());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(log.getDataHora()));
            stmt.setString(4, log.getDetalhes());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar log do servidor", e);
        }
    }
}