package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.ServidorDAO;
import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

import java.util.List;

/**
 * Classe de serviço responsável por aplicar regras de negócio e delegar ao DAO
 * a persistência dos dados do Servidor.
 */
public class ServidorService {

    private final ServidorDAO servidorDAO = new ServidorDAO();

    /**
     * Cria um novo servidor.
     * Apenas o gerente pode chamar este método.
     *
     * @param servidor  Dados do servidor a ser criado.
     * @param gerenteId ID do gerente que está realizando a ação.
     */
    public void criarServidor(Servidor servidor, int gerenteId) {
        try {
            servidorDAO.criarServidor(servidor, gerenteId);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao criar servidor: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza os dados do servidor.
     * Apenas o gerente pode chamar este método.
     */
    public void atualizarServidor(Servidor servidor, int gerenteId) {
        try {
            servidorDAO.atualizarServidor(servidor, gerenteId);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar servidor: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza as permissões do servidor.
     * Apenas o gerente pode chamar este método.
     */
    public void atualizarPermissoes(Servidor servidor, int gerenteId) {
        try {
            servidorDAO.atualizarPermissoes(servidor, gerenteId);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar permissões do servidor: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza a senha de um servidor.
     * Pode ser chamado pelo próprio servidor.
     */
    public void atualizarSenha(int servidorId, String novaSenha) {
        try {
            servidorDAO.atualizarSenha(servidorId, novaSenha);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar senha: " + e.getMessage(), e);
        }
    }

    /**
     * Desativa um servidor (não exclui do banco).
     * Apenas o gerente pode chamar este método.
     */
    public void desativarServidor(int servidorId, int gerenteId) {
        try {
            servidorDAO.desativarServidor(servidorId, gerenteId);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao desativar servidor: " + e.getMessage(), e);
        }
    }

    /**
     * Exclui um servidor permanentemente.
     * Apenas o gerente pode chamar este método.
     */
    public void excluirServidor(int servidorId, int gerenteId) {
        try {
            servidorDAO.excluirServidor(servidorId, gerenteId);
        } catch (ObjectNotFoundException e) {
            throw e; // Repassa a exceção específica
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao excluir servidor: " + e.getMessage(), e);
        }
    }

    /**
     * Busca um servidor pelo ID.
     * Apenas o gerente pode chamar este método.
     */
    public Servidor buscarPorId(int servidorId) {
        try {
            return servidorDAO.buscarPorId(servidorId);
        } catch (ObjectNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao buscar servidor: " + e.getMessage(), e);
        }
    }

    /**
     * Gera um relatório com todos os servidores cadastrados.
     * Apenas o gerente pode chamar este método.
     */
    public List<Servidor> gerarRelatorioServidores() {
        try {
            return servidorDAO.gerarRelatorioServidores();
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao gerar relatório de servidores: " + e.getMessage(), e);
        }
    }
    public Servidor buscarPorMatricula(int matricula) {
        try {
            return servidorDAO.buscarPorMatricula(matricula);
        } catch (ObjectNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao buscar servidor: " + e.getMessage(), e);
        }
    }

}