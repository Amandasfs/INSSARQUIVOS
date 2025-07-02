package inss.gca.mogi.mogi.dao;

import inss.gca.mogi.mogi.config.DatabaseConfig;
import inss.gca.mogi.mogi.dto.CaixaDTO;
import inss.gca.mogi.mogi.model.Caixa;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaixaDAO {

    public void cadastrar(Caixa caixa) {
        String sql = "INSERT INTO caixa (cod_caixa, prateleira, rua, andar, nb_inicial, nb_final, id_servidor) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
            throw new DataIntegrityViolationException("Erro ao cadastrar caixa", e);
        }
    }

    public List<Caixa> buscarTodas() {
        String sql = "SELECT * FROM caixa";
        List<Caixa> caixas = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                caixas.add(mapResultSetToCaixa(rs));
            }

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao buscar caixas", e);
        }

        return caixas;
    }

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

    public Caixa buscarCaixaPorNB(String nb) {
        String sql = "SELECT cod_caixa, nb_inicial, nb_final FROM caixa";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nbInicial = rs.getString("nb_inicial");
                String nbFinal = rs.getString("nb_final");

                if (nb.compareTo(nbInicial) >= 0 && nb.compareTo(nbFinal) <= 0) {
                    Caixa caixa = new Caixa();
                    caixa.setCodCaixa(rs.getString("cod_caixa"));
                    return caixa;
                }
            }

        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao realizar busca por NB", e);
        }

        return null;
    }

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

                    // Agora buscar NBs cadastrados para essa caixa
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


}
