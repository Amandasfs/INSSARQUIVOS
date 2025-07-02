package inss.gca.mogi.mogi.service.exceptions;

public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException(String mensagem) {
        super(mensagem);
    }
}
