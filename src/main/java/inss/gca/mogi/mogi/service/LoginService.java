package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.LoginDAO;
import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

/**
 * Serviço responsável por conter a lógica de negócio relacionada à autenticação de utilizadores.
 * Interage com o {@link LoginDAO} e lança exceções apropriadas para a camada de apresentação.
 */
public class LoginService {

    private final LoginDAO loginDAO;

    /**
     * Construtor padrão inicializando a dependência DAO.
     */
    public LoginService() {
        this.loginDAO = new LoginDAO();
    }

    /**
     * Autentica um servidor com base na matrícula e senha.
     *
     * @param matricula matrícula informada no login
     * @param senha     senha informada no ‘login’
     * @return um objeto {@link Servidor} autenticado com os seus dados
     * @throws ObjectNotFoundException se as credenciais forem inválidas
     * @throws DataIntegrityViolationException se ocorrer erro interno inesperado
     */
    public Servidor autenticar(int matricula, String senha) {
        try {
            Servidor servidor = loginDAO.autenticar(matricula, senha);
            if (servidor == null) {
                throw new ObjectNotFoundException("Matrícula ou senha inválidos.");
            }
            return servidor;
        } catch (ObjectNotFoundException e) {
            throw e; // Repassa a exceção para o controller tratar
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao realizar login.", e);
        }
    }

    /**
     * Verifica se existe um servidor cadastrado com a matrícula informada.
     *
     * @param matricula matrícula do servidor
     * @return true se existir, false caso contrário
     * @throws DataIntegrityViolationException em caso de falha na comunicação com o banco
     */
    public boolean usuarioExiste(int matricula) {
        try {
            return loginDAO.usuarioExiste(matricula);
        } catch (Exception e) {
            throw new DataIntegrityViolationException("Erro ao verificar existência do usuário.", e);
        }
    }
}