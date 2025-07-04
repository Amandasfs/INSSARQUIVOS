package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.ServidorDAO;
import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.PermissionDeniedException;

import java.util.List;

/**
 * Camada de serviço para manipulação dos dados de Servidor.
 * Realiza validações e encapsula a lógica entre Controller e DAO.
 */
public class ServidorService {

    private final ServidorDAO servidorDAO;

    public ServidorService() {
        this.servidorDAO = new ServidorDAO();
    }

    public void criarServidor(Servidor servidor, int gerenteId) {
        validarPermissaoGerente(gerenteId);
        servidorDAO.criarServidor(servidor, gerenteId);
    }

    public void atualizarServidor(Servidor servidor, int gerenteId) {
        validarPermissaoGerente(gerenteId);
        servidorDAO.atualizarServidor(servidor, gerenteId);
    }

    public void atualizarPermissoes(Servidor servidor, int gerenteId) {
        validarPermissaoGerente(gerenteId);
        servidorDAO.atualizarPermissoes(servidor, gerenteId);
    }

    public void atualizarSenha(int servidorId, String novaSenha) {
        // Pode ser que o próprio servidor altere a senha, sem precisar validar gerente
        servidorDAO.atualizarSenha(servidorId, novaSenha);
    }

    public Servidor buscarPorId(int id) {
        return servidorDAO.buscarPorId(id);
    }

    public Servidor buscarPorMatricula(int matricula) {
        return servidorDAO.buscarPorMatricula(matricula);
    }

    public Servidor buscarPorMatricula(String matricula) {
        return servidorDAO.buscarPorMatricula(matricula);
    }

    public Servidor autenticar(String matricula, String senha) {
        return servidorDAO.autenticar(matricula, senha);
    }

    public List<Servidor> gerarRelatorioServidores(int gerenteId) {
        validarPermissaoGerente(gerenteId);
        return servidorDAO.gerarRelatorioServidores();
    }

    /**
     * Valida se o servidor é gerente para operações restritas.
     * @throws PermissionDeniedException caso não seja gerente ou inativo.
     */
    private void validarPermissaoGerente(int matricula) {
        Servidor servidor = buscarPorMatricula(matricula);
        if (!servidor.isStatusPerfil()) {
            throw new PermissionDeniedException("Servidor está inativo.");
        }
        if (!servidor.getTipoPerfil().name().equalsIgnoreCase("GERENTE")) {
            throw new PermissionDeniedException("Permissão negada: somente gerentes podem executar esta operação.");
        }
    }
    /**
     * Reativa um servidor desativado (status_perfil = true).
     * Apenas gerentes podem executar essa ação.
     *
     * @param matriculaServidorReativado Matrícula do servidor a ser reativado
     * @param matriculaGerente           Matrícula do gerente logado
     */
    public void reativarServidor(int matriculaServidorReativado, int matriculaGerente) {
        // Valida se o solicitante tem permissão
        PermissaoValidator.validarPodeReativarPerfil(matriculaGerente);

        // Busca o servidor a ser reativado
        Servidor servidor = servidorDAO.buscarPorMatricula(matriculaServidorReativado);

        // Verifica se já está ativo
        if (servidor.isStatusPerfil()) {
            throw new IllegalStateException("Este servidor já está com o perfil ativo.");
        }

        // Reativa
        servidorDAO.reativarServidor(servidor.getId(), matriculaGerente);
    }

}
