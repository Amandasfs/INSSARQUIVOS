package inss.gca.mogi.mogi.dao;

import inss.gca.mogi.mogi.config.DatabaseConfig;
import inss.gca.mogi.mogi.dto.BuscaDTO;
import inss.gca.mogi.mogi.model.Segurado;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável por realizar operações CRUD na entidade Segurado.
 * Controla acesso ao banco de dados, incluindo validação de permissões.
 */
public class SeguradoDAO {

    /**
     * Insere um novo segurado no banco.
     * @param segurado objeto com os dados a serem inseridos.
     */
    public void criar(Segurado segurado) {
        PermissaoValidator.validarPodeCadastrar();

        String sql = "INSERT INTO segurado (nome_segurado, cpf, id_servidor) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, segurado.getNomeSegurado());
            stmt.setString(2, segurado.getCpf());
            stmt.setInt(3, segurado.getIdServidor());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    segurado.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar segurado", e);
        }
    }


    /**
     * Busca segurado pelo ID.
     * @param id identificador do segurado.
     * @return objeto Segurado encontrado.
     * @throws ObjectNotFoundException se segurado não existir.
     */
    public Segurado buscarPorId(int id) {
        String sql = "SELECT * FROM segurado WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Segurado segurado = new Segurado();
                    segurado.setId(rs.getInt("id"));
                    segurado.setNomeSegurado(rs.getString("nome_segurado"));
                    segurado.setCpf(rs.getString("cpf"));
                    segurado.setIdServidor(rs.getInt("id_servidor"));
                    return segurado;
                } else {
                    throw new ObjectNotFoundException("Segurado não encontrado com ID: " + id);
                }
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao buscar segurado", e);
        }
    }

    /**
     * Busca arquivos associados a um CPF, retornando lista de DTOs.
     * @param cpf CPF do segurado (limpo de formatação).
     * @return lista de BuscaDTO com arquivos relacionados.
     */
    public List<BuscaDTO> buscarPorCpf(String cpf) {
        String sql = """
        SELECT a.id, a.cod_caixa, a.nb, a.tipo_beneficio,
               s.cpf AS cpf_segurado, s.nome_segurado,
               c.rua, c.prateleira, c.andar
        FROM arquivo a
        JOIN segurado s ON a.id_segurado = s.id
        JOIN caixa c ON a.cod_caixa = c.cod_caixa
        WHERE regexp_replace(s.cpf, '[^0-9]', '', 'g') = ?
        """;

        List<BuscaDTO> resultados = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf.replaceAll("\\D", ""));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BuscaDTO dto = new BuscaDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setCodCaixa(rs.getString("cod_caixa"));
                    dto.setNb(rs.getString("nb"));
                    dto.setTipoBeneficio(rs.getString("tipo_beneficio"));
                    dto.setCpfSegurado(rs.getString("cpf_segurado"));
                    dto.setNomeSegurado(rs.getString("nome_segurado"));
                    dto.setRua(rs.getString("rua"));
                    dto.setPrateleira(rs.getInt("prateleira"));
                    dto.setAndar(rs.getString("andar"));
                    resultados.add(dto);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar arquivos por CPF: " + e.getMessage(), e);
        }

        return resultados;
    }

    /**
     * Retorna lista com todos os segurados cadastrados.
     */
    public List<Segurado> buscarTodos() {
        String sql = "SELECT * FROM segurado";
        List<Segurado> segurados = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Segurado segurado = new Segurado();
                segurado.setId(rs.getInt("id"));
                segurado.setNomeSegurado(rs.getString("nome_segurado"));
                segurado.setCpf(rs.getString("cpf"));
                segurado.setIdServidor(rs.getInt("id_servidor"));
                segurados.add(segurado);
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao buscar segurados", e);
        }

        return segurados;
    }

    /**
     * Atualiza o CPF de um segurado.
     */
    public void atualizarCPF(int id, String novoCpf) {
        try {
            int idServidor = obterIdServidorPorSegurado(id);
            PermissaoValidator.validarPodeAlterarCpfNb(idServidor);

            String sql = "UPDATE segurado SET cpf = ? WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, novoCpf);
                stmt.setInt(2, id);

                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    throw new ObjectNotFoundException("Segurado não encontrado para atualizar CPF.");
                }
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar CPF do segurado", e);
        }
    }

    /**
     * Atualiza o nome do segurado.
     */
    public void atualizarNome(int id, String novoNome) {
        int idServidor = obterIdServidorPorSegurado(id);
        PermissaoValidator.validarPodeAlterarNomeSegurado(idServidor);

        String sql = "UPDATE segurado SET nome_segurado = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoNome);
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar nome", e);
        }
    }

    /**
     * Exclui um segurado pelo ID.
     */
    public void deletar(int id) {
        try {
            int idServidor = obterIdServidorPorSegurado(id);
            PermissaoValidator.validarPodeExcluir(idServidor);

            String sql = "DELETE FROM segurado WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);

                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    throw new ObjectNotFoundException("Segurado não encontrado para exclusão.");
                }
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao deletar segurado", e);
        }
    }

    /**
     * Busca um segurado pelo CPF (único).
     */
    public Segurado buscarPorCpfUnico(String cpf) {
        String sql = "SELECT * FROM segurado WHERE cpf = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Segurado segurado = new Segurado();
                    segurado.setId(rs.getInt("id"));
                    segurado.setNomeSegurado(rs.getString("nome_segurado"));
                    segurado.setCpf(rs.getString("cpf"));
                    segurado.setIdServidor(rs.getInt("id_servidor"));
                    return segurado;
                } else {
                    throw new ObjectNotFoundException("Segurado não encontrado com CPF: " + cpf);
                }
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao buscar segurado por CPF.", e);
        }
    }

    /**
     * Retorna o ID do servidor que cadastrou o segurado.
     * @throws SQLException caso ocorra erro na consulta.
     */
    private int obterIdServidorPorSegurado(int idSegurado) {
        String sql = "SELECT id_servidor FROM segurado WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSegurado);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_servidor");
            }
            throw new RuntimeException("Segurado não encontrado");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar servidor", e);
        }
    }

    /**
     * Exclui o segurado se não possuir arquivos vinculados.
     */
    public void deletarSeNaoTiverArquivos(int id) {
        try {
            if (new ArquivoDAO().existeArquivoDoSegurado(id)) {
                throw new RuntimeException("Não é possível excluir o segurado: ainda existem arquivos vinculados.");
            }

            int idServidor = obterIdServidorPorSegurado(id);
            PermissaoValidator.validarPodeExcluir(idServidor);

            String sql = "DELETE FROM segurado WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);

                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    throw new ObjectNotFoundException("Segurado não encontrado para exclusão.");
                }

            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao excluir segurado.", e);
        }
    }
}
