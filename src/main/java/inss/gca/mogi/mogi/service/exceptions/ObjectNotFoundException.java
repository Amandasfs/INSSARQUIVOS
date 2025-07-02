package inss.gca.mogi.mogi.service.exceptions;

/**
 * Exceção lançada quando um objeto não é encontrado no banco de dados.
 * Usada para sinalizar erros de consulta que não retornam resultados.
 */
public class ObjectNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem explicando o erro.
     * @param cause Causa original da exceção.
     */
    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor apenas com mensagem.
     *
     * @param message Mensagem explicando o erro.
     */
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
