package inss.gca.mogi.mogi.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável pela configuração e fornecimento da conexão
 * com o banco de dados PostgreSQL.
 *
 * Local ideal para futuras configurações como pool de conexões ou integração com ORM.
 */
public class DatabaseConfig {

    // URL de conexão com o banco (hostname, porta, nome do banco)
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/bancodedadosmogi";

    // Usuário e senha do banco
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "postgres";

    /**
     * Obtém uma nova conexão com o banco de dados PostgreSQL.
     *
     * @return uma instância ativa de {@link Connection}
     * @throws RuntimeException caso ocorra erro na conexão
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados", e);
        }
    }

    /**
     * Método de teste da conexão com o banco de dados.
     * Ideal para uso em ambiente de desenvolvimento.
     */
    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                System.out.println("Banco conectado com sucesso!");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados.");
            e.printStackTrace();
        }
    }
}