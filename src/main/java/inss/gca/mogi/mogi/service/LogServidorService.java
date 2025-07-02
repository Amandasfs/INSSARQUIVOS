package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.LogServidorDAO;
import inss.gca.mogi.mogi.model.LogServidor;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

/**
 * Serviço responsável pela lógica de negócio relacionada aos logs dos servidores.
 * Atua como intermediário entre os controladores e a camada DAO.
 */
public class LogServidorService {

    private final LogServidorDAO logServidorDAO;

    /**
     * Construtor padrão com inicialização da camada DAO.
     */
    public LogServidorService() {
        this.logServidorDAO = new LogServidorDAO();
    }

    /**
     * Registra uma ação realizada por um servidor no sistema.
     *
     * @param log Objeto contendo os dados da ação (ID do servidor, ação, data/hora e detalhes).
     * @throws DataIntegrityViolationException caso ocorra erro de integridade ao inserir o log.
     */
    public void registrarAcao(LogServidor log) {
        try {
            if (log == null || log.getServidorId() <= 0 || log.getAcao() == null || log.getDataHora() == null) {
                throw new DataIntegrityViolationException("Dados de log incompletos ou inválidos.");
            }

            logServidorDAO.registrarLog(log);

        } catch (Exception e) {
            // Envolve qualquer exceção não tratada para manter coesão da exceção de serviço
            throw new DataIntegrityViolationException("Erro ao registrar log: " + e.getMessage(), e);
        }
    }

    /**
     * Método de exemplo para demonstrar uma possível futura busca por logs.
     * Aqui você pode aplicar regras de autorização ou paginação.
     *
     * @throws ObjectNotFoundException se não houver logs encontrados (exemplo).
     */
    public void buscarLogsPorServidor(int servidorId) {
        // Método futuro: implementar busca com LogServidorDAO.listarPorServidorId()
        throw new ObjectNotFoundException("Funcionalidade de busca de logs ainda não implementada.");
    }
}
