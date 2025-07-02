package inss.gca.mogi.mogi.dao;

import inss.gca.mogi.mogi.config.DatabaseConfig;
import inss.gca.mogi.mogi.dto.BuscaDTO;
import inss.gca.mogi.mogi.model.Arquivo;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArquivoDAO {

    public void criar(Arquivo arquivo) {
        try {
            PermissaoValidator.validarPodeCadastrar(arquivo.getIdServidor());

            String sql = "INSERT INTO arquivo (nb, tipo_beneficio, id_segurado, cod_caixa, id_servidor) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, arquivo.getNb());
                stmt.setString(2, arquivo.getTipoBeneficio());
                stmt.setInt(3, arquivo.getIdSegurado());
                stmt.setString(4, arquivo.getCodCaixa());
                stmt.setInt(5, arquivo.getIdServidor());

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao criar arquivo.", e);
        }
    }

    public void atualizar(Arquivo arquivo) {
        try {
            PermissaoValidator.validarPodeAlterar(arquivo.getIdServidor());

            String sql = "UPDATE arquivo SET nb = ?, tipo_beneficio = ?, id_segurado = ?, cod_caixa = ?, id_servidor = ? WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, arquivo.getNb());
                stmt.setString(2, arquivo.getTipoBeneficio());
                stmt.setInt(3, arquivo.getIdSegurado());
                stmt.setString(4, arquivo.getCodCaixa());
                stmt.setInt(5, arquivo.getIdServidor());
                stmt.setInt(6, arquivo.getId());

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar arquivo.", e);
        }
    }

    public void deletar(int id) {
        int idServidor = obterIdServidorPorArquivo(id);

        try {
            PermissaoValidator.validarPodeExcluir(idServidor);

            String sql = "DELETE FROM arquivo WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao deletar arquivo.", e);
        }
    }

    public Arquivo buscarPorId(int id) {
        String sql = "SELECT * FROM arquivo WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Arquivo(
                            rs.getInt("id"),
                            rs.getString("nb"),
                            rs.getString("tipo_beneficio"),
                            rs.getInt("id_segurado"),
                            rs.getString("cod_caixa"),
                            rs.getInt("id_servidor")
                    );
                } else {
                    throw new ObjectNotFoundException("Arquivo não encontrado com ID: " + id);
                }
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao buscar arquivo por ID.", e);
        }
    }

    public BuscaDTO buscarPorNb(String nb) {
        String nbLimpo = nb.replaceAll("\\D", "");

        // 1. Busca arquivo cadastrado
        String sqlArquivo = """
        SELECT a.id, a.cod_caixa, a.nb, a.tipo_beneficio,
               s.cpf AS cpf_segurado, s.nome_segurado,
               c.rua, c.prateleira, c.andar
        FROM arquivo a
        JOIN segurado s ON a.id_segurado = s.id
        JOIN caixa c ON a.cod_caixa = c.cod_caixa
        WHERE a.nb = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlArquivo)) {

            stmt.setString(1, nbLimpo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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
                    return dto;
                }
            }

            // 2. Se não achou arquivo, busca caixa que contenha o NB no intervalo
            String sqlCaixa = "SELECT cod_caixa, prateleira, rua, andar, nb_inicial, nb_final FROM caixa";

            try (PreparedStatement stmtCaixa = conn.prepareStatement(sqlCaixa);
                 ResultSet rsCaixa = stmtCaixa.executeQuery()) {

                while (rsCaixa.next()) {
                    String nbInicial = rsCaixa.getString("nb_inicial").replaceAll("\\D", "");
                    String nbFinal = rsCaixa.getString("nb_final").replaceAll("\\D", "");

                    if (nbLimpo.compareTo(nbInicial) >= 0 && nbLimpo.compareTo(nbFinal) <= 0) {
                        BuscaDTO caixaDto = new BuscaDTO();
                        caixaDto.setId(0); // id 0 indica arquivo não cadastrado
                        caixaDto.setCodCaixa(rsCaixa.getString("cod_caixa"));
                        caixaDto.setNb(nb); // NB pesquisado
                        caixaDto.setRua(rsCaixa.getString("rua"));
                        caixaDto.setPrateleira(rsCaixa.getInt("prateleira"));
                        caixaDto.setAndar(rsCaixa.getString("andar"));
                        // Pode colocar uma flag ou mensagem adicional para avisar que arquivo não cadastrado mas pertence a caixa
                        return caixaDto;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar arquivo por NB: " + e.getMessage(), e);
        }

        // Nenhuma caixa nem arquivo encontrados para esse NB
        throw new RuntimeException("Arquivo não cadastrado nem pertence a nenhuma caixa para NB: " + nb);
    }


    public void atualizarNb(int id, String novoNb) {
        int idServidor = obterIdServidorPorArquivo(id);

        try {
            PermissaoValidator.validarPodeAlterarCpfNb(idServidor);

            String sql = "UPDATE arquivo SET nb = ? WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, novoNb);
                stmt.setInt(2, id);

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar NB do arquivo.", e);
        }
    }

    public void atualizarCaixa(int id, String novoCodCaixa) {
        int idServidor = obterIdServidorPorArquivo(id);

        try {
            PermissaoValidator.validarPodeAlterarCaixa(idServidor);

            String sql = "UPDATE arquivo SET cod_caixa = ? WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, novoCodCaixa);
                stmt.setInt(2, id);

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar caixa do arquivo.", e);
        }
    }

    public List<Arquivo> listarTodos() {
        List<Arquivo> lista = new ArrayList<>();
        String sql = "SELECT * FROM arquivo";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Arquivo a = new Arquivo(
                        rs.getInt("id"),
                        rs.getString("nb"),
                        rs.getString("tipo_beneficio"),
                        rs.getInt("id_segurado"),
                        rs.getString("cod_caixa"),
                        rs.getInt("id_servidor")
                );
                lista.add(a);
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao listar arquivos.", e);
        }

        return lista;
    }

    /**
     * Obtém o ID do servidor responsável por um determinado arquivo.
     */
    public int obterIdServidorPorArquivo(int idArquivo) {
        String sql = "SELECT id_servidor FROM arquivo WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idArquivo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_servidor");
                } else {
                    throw new ObjectNotFoundException("Arquivo não encontrado. ID: " + idArquivo);
                }
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Erro ao buscar ID do servidor pelo arquivo.", e);
        }
    }
}