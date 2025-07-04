package inss.gca.mogi.mogi.config;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe utilitária para operações de segurança relacionadas a senha,
 * como criptografia e verificação utilizando o algoritmo BCrypt.
 */
public class PasswordUtil {

    private static final int WORKLOAD = 10; // Custo do algoritmo (padrão seguro: 10)

    /**
     * Gera o hash criptografado de uma senha em texto claro usando BCrypt.
     *
     * @param plainPassword senha original em texto claro
     * @return hash criptografado da senha
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORKLOAD));
    }

    /**
     * Verifica se uma senha em texto claro corresponde a um hash BCrypt existente.
     *
     * @param plainPassword  senha informada pelo usuário
     * @param hashedPassword hash da senha armazenado no banco
     * @return true se a senha for válida; false caso contrário
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    /**
     * Alias para verifyPassword - usado para dar semântica diferente em outros contextos.
     */
    public static boolean checkPassword(String senhaDigitada, String senhaHashArmazenada) {
        return verifyPassword(senhaDigitada, senhaHashArmazenada);
    }

    /**
     * Método de teste para demonstração da criptografia e validação de senha.
     * Pode ser usado em ambiente de desenvolvimento.
     */
    public static void main(String[] args) {
        String senhaOriginal = "12345678";

        // Criptografa a senha
        String hash = hashPassword(senhaOriginal);
        System.out.println("Hash gerado: " + hash);

        // Testa se a senha confere com o hash
        boolean senhaValida = verifyPassword(senhaOriginal, hash);
        System.out.println(senhaValida ? "Senha correta!" : "Senha incorreta!");

        // Teste manual com hash conhecido
        String senhaTentada = "amanda12";
        String hashDoBanco = "$2a$10$opCnf0RRhXDbWy.joJNsOu3ggcJtFboCsaTkwJ6SH4kCsJ9R6gMW2";

        boolean valido = checkPassword(senhaTentada, hashDoBanco);
        System.out.println(valido ? "Senha confere!" : "Senha incorreta!");
    }
}
