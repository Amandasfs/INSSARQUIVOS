package inss.gca.mogi.mogi.service.exceptions;

/**
 * Exceção lançada quando uma operação viola a integridade dos dados.
 * Exemplo: tentativa de exclusão com vínculos, dados inválidos ou duplicados.
 */
public class DataIntegrityViolationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem explicando o erro.
     * @param cause Causa original da exceção.
     */
    public DataIntegrityViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor apenas com mensagem.
     *
     * @param message Mensagem explicando o erro.
     */
    public DataIntegrityViolationException(String message) {
        super(message);
    }
}
