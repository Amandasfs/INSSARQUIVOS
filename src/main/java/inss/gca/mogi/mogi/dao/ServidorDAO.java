package inss.gca.mogi.mogi.dao;

import inss.gca.mogi.mogi.config.DatabaseConfig;
import inss.gca.mogi.mogi.config.PasswordUtil;
import inss.gca.mogi.mogi.model.LogServidor;
import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.model.TipoPerfil;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por interagir com a tabela de servidores no banco de dados.
 * Contém operações CRUD, controle de permissões e auditoria via logs.
 */
public class ServidorDAO {

    private final LogServidorDAO logDAO = new LogServidorDAO();

    /**
     * Cria um novo servidor no banco de dados.
     * Apenas gerentes devem ter acesso a esta operação.
     */
    public void criarServidor(Servidor servidor, int gerenteId) {
        String sql = "INSERT INTO servidor (nome, senha, matricula, tipo_perfil, status_perfil, " +
                "pode_cadastrar, pode_alterar, pode_alterar_nome_segurado, pode_alterar_caixa, " +
                "pode_alterar_cpf_nb, pode_alterar_local_caixa, pode_alterar_codigo_caixa, pode_excluir) " +
                "VALUES (?, ?, ?, ?::tipo_perfil, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, servidor.getNome());
            stmt.setString(2, PasswordUtil.hashPassword(servidor.getSenha()));
            stmt.setInt(3, servidor.getMatricula());
            stmt.setString(4, servidor.getTipoPerfil().name());
            stmt.setBoolean(5, servidor.isStatusPerfil());
            stmt.setBoolean(6, servidor.isPodeCadastrar());
            stmt.setBoolean(7, servidor.isPodeAlterar());
            stmt.setBoolean(8, servidor.isPodeAlterarNomeSegurado());
            stmt.setBoolean(9, servidor.isPodeAlterarCaixa());
            stmt.setBoolean(10, servidor.isPodeAlterarCpfNb());
            stmt.setBoolean(11, servidor.isPodeAlterarLocalCaixa());
            stmt.setBoolean(12, servidor.isPodeAlterarCodigoCaixa());
            stmt.setBoolean(13, servidor.isPodeExcluir());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                servidor.setId(rs.getInt(1));
            }

            logDAO.registrarLog(new LogServidor(
                    gerenteId,
                    "CRIAR_SERVIDOR",
                    LocalDateTime.now(),
                    "Servidor criado: " + servidor.getNome()
            ));

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao criar servidor", e);
        }
    }

    /**
     * Atualiza os dados básicos de um servidor.
     * Apenas gerentes devem ter acesso a esta operação.
     */
    public void atualizarServidor(Servidor servidor, int gerenteId) {
        String sql = "UPDATE servidor SET nome = ?, matricula = ?, tipo_perfil = ?::tipo_perfil, status_perfil = ? WHERE matricula = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, servidor.getNome());
            stmt.setInt(2, servidor.getMatricula());
            stmt.setString(3, servidor.getTipoPerfil().name());
            stmt.setBoolean(4, servidor.isStatusPerfil());
            stmt.setInt(5, servidor.getMatricula());  // Usa matrícula no WHERE
            stmt.executeUpdate();

            logDAO.registrarLog(new LogServidor(
                    gerenteId,
                    "ATUALIZAR_SERVIDOR",
                    LocalDateTime.now(),
                    "Servidor matrícula: " + servidor.getMatricula()
            ));

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar servidor", e);
        }
    }


    /**
     * Atualiza permissões de um servidor.
     * Apenas gerentes devem ter acesso a esta operação.
     */
    public void atualizarPermissoes(Servidor servidor) {
        PermissaoValidator.validarGerente(); // Só gerentes podem chamar este método

        String sql = "UPDATE servidor SET pode_cadastrar = ?, pode_alterar = ?, "
                + "pode_alterar_nome_segurado = ?, pode_alterar_caixa = ?, "
                + "pode_alterar_cpf_nb = ?, pode_alterar_local_caixa = ?, "
                + "pode_alterar_codigo_caixa = ?, pode_excluir = ? "
                + "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, servidor.isPodeCadastrar());
            stmt.setBoolean(2, servidor.isPodeAlterar());
            stmt.setBoolean(3, servidor.isPodeAlterarNomeSegurado());
            stmt.setBoolean(4, servidor.isPodeAlterarCaixa());
            stmt.setBoolean(5, servidor.isPodeAlterarCpfNb());
            stmt.setBoolean(6, servidor.isPodeAlterarLocalCaixa());
            stmt.setBoolean(7, servidor.isPodeAlterarCodigoCaixa());
            stmt.setBoolean(8, servidor.isPodeExcluir());
            stmt.setInt(9, servidor.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar permissões", e);
        }
    }


    /**
     * Atualiza a senha de um servidor. Qualquer servidor pode usar para alterar a própria senha.
     */
    public void atualizarSenha(int servidorId, String novaSenha) {
        String sql = "UPDATE servidor SET senha = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String senhaCriptografada = PasswordUtil.hashPassword(novaSenha);
            stmt.setString(1, senhaCriptografada);
            stmt.setInt(2, servidorId);
            stmt.executeUpdate();

            logDAO.registrarLog(new LogServidor(
                    servidorId,
                    "ATUALIZAR_SENHA",
                    LocalDateTime.now(),
                    "Senha alterada"
            ));

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar senha", e);
        }
    }

    /**
     * Desativa um servidor no sistema (não exclui fisicamente).
     * Apenas gerentes devem ter acesso a esta operação.
     */
    public void desativarServidor(int servidorId, int gerenteId) {
        String sql = "UPDATE servidor SET status_perfil = false WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, servidorId);
            stmt.executeUpdate();

            logDAO.registrarLog(new LogServidor(
                    gerenteId,
                    "DESATIVAR_SERVIDOR",
                    LocalDateTime.now(),
                    "Servidor ID: " + servidorId
            ));

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao desativar servidor", e);
        }
    }

    /**
     * Busca um servidor por ID.
     */
    public Servidor buscarPorId(int servidorId) {
        String sql = "SELECT * FROM servidor WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, servidorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return popularServidor(rs);
            } else {
                throw new ObjectNotFoundException("Servidor com ID " + servidorId + " não encontrado.");
            }

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao buscar servidor", e);
        }
    }

    /**
     * Busca um servidor por matrícula (int).
     */
    public Servidor buscarPorMatricula(int matricula) {
        String sql = "SELECT * FROM servidor WHERE matricula = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Servidor servidor = popularServidor(rs);
                servidor.definirPermissoesPadrao(); // Garante permissões consistentes
                return servidor;
            }
            throw new ObjectNotFoundException("Servidor não encontrado");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar servidor", e);
        }
    }

    public Servidor autenticar(String matricula, String senha) {
        String sql = "SELECT * FROM servidor WHERE matricula = ? AND senha = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, matricula);
            stmt.setString(2, PasswordUtil.hashPassword(senha));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Servidor servidor = popularServidor(rs);
                servidor.definirPermissoesPadrao();
                return servidor;
            }
            throw new ObjectNotFoundException("Credenciais inválidas");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar", e);
        }
    }
    /**
     * Popular objeto Servidor a partir do ResultSet.
     */
    private Servidor popularServidor(ResultSet rs) throws SQLException {
        Servidor servidor = new Servidor();
        servidor.setId(rs.getInt("id"));
        servidor.setNome(rs.getString("nome"));
        servidor.setSenha(rs.getString("senha"));
        servidor.setMatricula(rs.getInt("matricula"));
        servidor.setTipoPerfil(TipoPerfil.valueOf(rs.getString("tipo_perfil")));
        servidor.setStatusPerfil(rs.getBoolean("status_perfil"));

        // Permissões
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
    /**
     * Gera uma lista completa de todos os servidores cadastrados no sistema.
     */
    public List<Servidor> gerarRelatorioServidores() {
        String sql = "SELECT * FROM servidor";
        List<Servidor> lista = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(popularServidor(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao gerar relatório de servidores", e);
        }
    }

    /**
     * Reativa um servidor no sistema (altera status_perfil para true).
     * Apenas gerentes devem ter acesso a esta operação.
     */
    public void reativarServidor(int servidorId, int gerenteId) {
        String sql = "UPDATE servidor SET status_perfil = true WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, servidorId);
            stmt.executeUpdate();

            logDAO.registrarLog(new LogServidor(
                    gerenteId,
                    "REATIVAR_SERVIDOR",
                    LocalDateTime.now(),
                    "Servidor ID: " + servidorId
            ));

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao reativar servidor", e);
        }
    }

}
