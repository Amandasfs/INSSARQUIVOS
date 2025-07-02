package inss.gca.mogi.mogi.dao;

import inss.gca.mogi.mogi.config.DatabaseConfig;
import inss.gca.mogi.mogi.config.PasswordUtil;
import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.model.TipoPerfil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe responsável por realizar operações de autenticação
 * e verificação de usuários no banco de dados.
 */
public class LoginDAO {

    /**
     * Realiza a autenticação do usuário com base na matrícula e senha.
     *
     * @param matricula matrícula do servidor
     * @param senha senha digitada no login
     * @return um objeto {@link Servidor} com dados do usuário, se login for bem-sucedido; caso contrário, null
     */
    public Servidor autenticar(int matricula, String senha) {
        String sql = "SELECT * FROM servidor WHERE matricula = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaHash = rs.getString("senha");

                    // Verifica se a senha informada corresponde ao hash armazenado
                    if (PasswordUtil.verifyPassword(senha, senhaHash)) {
                        // Login bem-sucedido - popula e retorna objeto Servidor
                        Servidor servidor = new Servidor();
                        servidor.setId(rs.getInt("id"));
                        servidor.setNome(rs.getString("nome"));
                        servidor.setSenha(senhaHash);
                        servidor.setMatricula(rs.getInt("matricula"));
                        servidor.setTipoPerfil(TipoPerfil.valueOf(rs.getString("tipo_perfil")));
                        servidor.setStatusPerfil(rs.getBoolean("status_perfil"));
                        servidor.setPodeCadastrar(rs.getBoolean("pode_cadastrar"));
                        servidor.setPodeAlterar(rs.getBoolean("pode_alterar"));
                        servidor.setPodeAlterarNomeSegurado(rs.getBoolean("pode_alterar_nome_segurado"));
                        servidor.setPodeAlterarCaixa(rs.getBoolean("pode_alterar_caixa"));
                        servidor.setPodeAlterarCpfNb(rs.getBoolean("pode_alterar_cpf_nb"));
                        servidor.setPodeAlterarLocalCaixa(rs.getBoolean("pode_alterar_local_caixa"));
                        servidor.setPodeAlterarCodigoCaixa(rs.getBoolean("pode_alterar_codigo_caixa"));
                        servidor.setPodeExcluir(rs.getBoolean("pode_excluir"));
                        return servidor;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar usuário: " + e.getMessage());
            e.printStackTrace();
        }

        // Retorna null caso o login falhe
        return null;
    }

    /**
     * Verifica se um usuário existe no banco com base na matrícula.
     *
     * @param matricula matrícula do servidor
     * @return true se o usuário existir, false caso contrário
     */
    public boolean usuarioExiste(int matricula) {
        String sql = "SELECT 1 FROM servidor WHERE matricula = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se encontrar resultado
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência do usuário: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}