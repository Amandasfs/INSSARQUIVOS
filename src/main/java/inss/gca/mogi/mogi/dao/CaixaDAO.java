package inss.gca.mogi.mogi.dao;

import inss.gca.mogi.mogi.config.DatabaseConfig;
import inss.gca.mogi.mogi.dto.CaixaDTO;
import inss.gca.mogi.mogi.model.Caixa;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe DAO responsável pelo acesso e manipulação dos dados da entidade Caixa no banco de dados.
 * Implementa as operações CRUD e consultas específicas para Caixa.
 */
public class CaixaDAO {

    /**
     * Insere uma nova Caixa no banco de dados.
     * @param caixa objeto Caixa a ser cadastrado
     * @throws DataIntegrityViolationException em caso de erro SQL durante a inserção
     */
    public void criar(Caixa caixa) {
        PermissaoValidator.validarPodeCadastrar();

        String sql = "INSERT INTO caixa (cod_caixa, prateleira, rua, andar, nb_inicial, nb_final, id_servidor) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, caixa.getCodCaixa());
            stmt.setInt(2, caixa.getPrateleira());
            stmt.setString(3, caixa.getRua());
            stmt.setString(4, caixa.getAndar());
            stmt.setString(5, caixa.getNbInicial());
            stmt.setString(6, caixa.getNbFinal());
            stmt.setInt(7, caixa.getIdServidor());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar caixa", e);
        }
    }

    /**
     * Recupera todas as caixas cadastradas no banco.
     * @return lista de objetos Caixa
     * @throws DataIntegrityViolationException em caso de erro SQL
     */
    public List<Caixa> buscarTodas() {
        String sql = "SELECT * FROM caixa";
        List<Caixa> caixas = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Itera sobre o ResultSet e converte cada registro em objeto Caixa
            while (rs.next()) {
                caixas.add(mapResultSetToCaixa(rs));
            }

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao buscar caixas", e);
        }

        return caixas;
    }

    /**
     * Atualiza os dados de localização da caixa.
     * @param codCaixa código da caixa a ser atualizada
     * @param rua rua nova
     * @param prateleira número da prateleira nova
     * @param andar andar novo
     * @throws ObjectNotFoundException se a caixa não existir
     * @throws DataIntegrityViolationException para erros SQL
     */
    public void atualizarLocal(String codCaixa, String rua, int prateleira, String andar) {
        String sql = "UPDATE caixa SET rua = ?, prateleira = ?, andar = ? WHERE cod_caixa = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rua);
            stmt.setInt(2, prateleira);
            stmt.setString(3, andar);
            stmt.setString(4, codCaixa);

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new ObjectNotFoundException("Caixa não encontrada para atualização de local.");

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar local da caixa", e);
        }
    }

    /**
     * Atualiza os números de benefício (NB) inicial e final da caixa.
     * @param codCaixa código da caixa
     * @param nbInicial novo NB inicial (sem formatação)
     * @param nbFinal novo NB final (sem formatação)
     * @throws ObjectNotFoundException se a caixa não for encontrada
     * @throws DataIntegrityViolationException em caso de erro SQL
     */
    public void atualizarNb(String codCaixa, String nbInicial, String nbFinal) {
        String sql = "UPDATE caixa SET nb_inicial = ?, nb_final = ? WHERE cod_caixa = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nbInicial);
            stmt.setString(2, nbFinal);
            stmt.setString(3, codCaixa);

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new ObjectNotFoundException("Caixa não encontrada para atualização de NB.");

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar NB da caixa", e);
        }
    }

    /**
     * Atualiza o código da caixa.
     * @param codCaixaAntigo código atual da caixa
     * @param codCaixaNovo novo código para a caixa
     * @throws ObjectNotFoundException se a caixa não existir
     * @throws DataIntegrityViolationException em caso de erro SQL
     */
    public void atualizarCodigo(String codCaixaAntigo, String codCaixaNovo) {
        String sql = "UPDATE caixa SET cod_caixa = ? WHERE cod_caixa = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codCaixaNovo);
            stmt.setString(2, codCaixaAntigo);

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new ObjectNotFoundException("Caixa não encontrada para atualização de código.");

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar código da caixa", e);
        }
    }

    /**
     * Remove uma caixa do banco.
     * @param codCaixa código da caixa a ser excluída
     * @throws ObjectNotFoundException se a caixa não existir
     * @throws DataIntegrityViolationException em caso de erro SQL
     */
    public void deletar(String codCaixa) {
        String sql = "DELETE FROM caixa WHERE cod_caixa = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codCaixa);

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new ObjectNotFoundException("Caixa não encontrada para exclusão.");

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao deletar caixa", e);
        }
    }

    /**
     * Verifica se existem arquivos vinculados a uma determinada caixa.
     * Usado para garantir deleção segura, evitando remover caixas em uso.
     * @param codCaixa código da caixa
     * @return true se existir ao menos um arquivo vinculado, false caso contrário
     */
    public boolean existeArquivoNaCaixa(String codCaixa) {
        String sql = "SELECT 1 FROM arquivo WHERE cod_caixa = ? LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codCaixa);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se encontrou algum registro
            }

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao verificar arquivos vinculados à caixa", e);
        }
    }

    /**
     * Busca a caixa que contém determinado número de benefício (NB).
     * Considera o intervalo NB inicial - NB final da caixa.
     * @param nb número de benefício a buscar (sem formatação)
     * @return objeto Caixa encontrado ou null se não existir
     * @throws DataIntegrityViolationException em caso de erro SQL
     */
    public Caixa buscarCaixaPorNB(String nb) {
        String sql = "SELECT cod_caixa, nb_inicial, nb_final FROM caixa";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Percorre todas as caixas para verificar se o NB está no intervalo
            while (rs.next()) {
                String nbInicial = rs.getString("nb_inicial");
                String nbFinal = rs.getString("nb_final");

                // Compara lexicograficamente o NB dentro do intervalo
                if (nb.compareTo(nbInicial) >= 0 && nb.compareTo(nbFinal) <= 0) {
                    Caixa caixa = new Caixa();
                    caixa.setCodCaixa(rs.getString("cod_caixa"));
                    return caixa;
                }
            }

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao realizar busca por NB", e);
        }

        // Retorna null se nenhuma caixa foi encontrada com o NB informado
        return null;
    }

    /**
     * Busca uma caixa pelo código.
     * @param codCaixa código da caixa
     * @return Optional contendo a Caixa encontrada ou vazio se não existir
     * @throws ObjectNotFoundException em caso de erro SQL
     */
    public Optional<Caixa> buscarCaixaPorCodigo(String codCaixa) {
        String sql = "SELECT * FROM caixa WHERE cod_caixa = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codCaixa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCaixa(rs));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new ObjectNotFoundException("Erro ao buscar caixa: " + e.getMessage(), e);
        }
    }

    /**
     * Mapeia uma linha do ResultSet para um objeto Caixa.
     * @param rs ResultSet da consulta SQL
     * @return objeto Caixa preenchido
     * @throws SQLException caso ocorra erro no acesso ao ResultSet
     */
    private Caixa mapResultSetToCaixa(ResultSet rs) throws SQLException {
        return new Caixa(
                rs.getString("cod_caixa"),
                rs.getInt("prateleira"),
                rs.getString("rua"),
                rs.getString("andar"),
                rs.getString("nb_inicial"),
                rs.getString("nb_final"),
                rs.getInt("id_servidor")
        );
    }

    /**
     * Busca o NB inicial da caixa pelo código.
     * @param codCaixa código da caixa
     * @return NB inicial ou null se não encontrado
     * @throws SQLException em caso de erro SQL
     */
    public String buscarNbInicial(String codCaixa) throws SQLException {
        String sql = "SELECT nb_inicial FROM caixa WHERE cod_caixa = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codCaixa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("nb_inicial");
            }
        }
        return null;
    }

    /**
     * Busca o NB final da caixa pelo código.
     * @param codCaixa código da caixa
     * @return NB final ou null se não encontrado
     * @throws SQLException em caso de erro SQL
     */
    public String buscarNbFinal(String codCaixa) throws SQLException {
        String sql = "SELECT nb_final FROM caixa WHERE cod_caixa = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codCaixa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("nb_final");
            }
        }
        return null;
    }

    /**
     * Busca uma caixa e seus números de benefício (NBs) associados,
     * retornando um DTO com dados completos e a lista de NBs.
     * @param codCaixa código da caixa
     * @return CaixaDTO com dados e lista de NBs
     * @throws RuntimeException se ocorrer erro ou caixa não encontrada
     */
    public CaixaDTO buscarPorCodigoComNbs(String codCaixa) {
        CaixaDTO caixaDto = null;

        String sqlCaixa = "SELECT cod_caixa, prateleira, rua, andar, nb_inicial, nb_final, id_servidor FROM caixa WHERE cod_caixa = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCaixa)) {

            stmt.setString(1, codCaixa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    caixaDto = new CaixaDTO();
                    caixaDto.setCodCaixa(rs.getString("cod_caixa"));
                    caixaDto.setPrateleira(rs.getInt("prateleira"));
                    caixaDto.setRua(rs.getString("rua"));
                    caixaDto.setAndar(rs.getString("andar"));
                    caixaDto.setNbInicial(rs.getString("nb_inicial"));
                    caixaDto.setNbFinal(rs.getString("nb_final"));
                    caixaDto.setIdServidor(rs.getInt("id_servidor"));

                    // Busca os NBs cadastrados para essa caixa
                    String sqlNbs = "SELECT nb FROM arquivo WHERE cod_caixa = ?";
                    try (PreparedStatement stmtNbs = conn.prepareStatement(sqlNbs)) {
                        stmtNbs.setString(1, codCaixa);
                        try (ResultSet rsNbs = stmtNbs.executeQuery()) {
                            List<String> nbs = new ArrayList<>();
                            while (rsNbs.next()) {
                                nbs.add(rsNbs.getString("nb"));
                            }
                            caixaDto.setNbs(nbs);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar caixa e arquivos: " + e.getMessage(), e);
        }

        if (caixaDto == null) {
            throw new RuntimeException("Caixa não encontrada para código: " + codCaixa);
        }

        return caixaDto;
    }
    public void atualizarLocalizacao(String codCaixa, String novoLocal) {
        int idServidor = obterIdServidorPorCaixa(codCaixa);
        PermissaoValidator.validarPodeAlterarLocalCaixa(idServidor);

        String sql = "UPDATE caixa SET rua = ?, andar = ? WHERE cod_caixa = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String[] partes = novoLocal.split("-");
            stmt.setString(1, partes[0]);
            stmt.setString(2, partes[1]);
            stmt.setString(3, codCaixa);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar localização", e);
        }
    }
    private int obterIdServidorPorCaixa(String codCaixa) {
        String sql = "SELECT id_servidor FROM caixa WHERE cod_caixa = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codCaixa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_servidor");
            }
            throw new RuntimeException("Caixa não encontrada");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar servidor", e);
        }
    }
}