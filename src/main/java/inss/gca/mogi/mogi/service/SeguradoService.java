package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.SeguradoDAO;
import inss.gca.mogi.mogi.dto.BuscaDTO;
import inss.gca.mogi.mogi.model.Segurado;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

import java.sql.SQLException;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio da entidade Segurado,
 * atuando como intermediário entre Controller e DAO.
 */
public class SeguradoService {

    private final SeguradoDAO seguradoDAO;

    public SeguradoService() {
        this.seguradoDAO = new SeguradoDAO();
    }

    /**
     * Cria novo segurado.
     * @param segurado objeto com dados do segurado.
     * @return segurado criado (com ID gerado).
     */
    public Segurado criarSegurado(Segurado segurado) {
        PermissaoValidator.validarPodeCadastrar(segurado.getIdServidor());
        try {
            seguradoDAO.criar(segurado);
            return segurado;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao criar segurado: dados inválidos ou CPF já existente.", e);
        }
    }

    /**
     * Busca segurado pelo ID.
     */
    public Segurado buscarPorId(int id) {
        return seguradoDAO.buscarPorId(id);
    }

    /**
     * Busca arquivos associados a um CPF.
     * @throws ObjectNotFoundException se não houver arquivos.
     */
    public List<BuscaDTO> buscarPorCpf(String cpf) {
        List<BuscaDTO> resultados = seguradoDAO.buscarPorCpf(cpf);
        if (resultados.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum arquivo encontrado para o CPF: " + cpf);
        }
        return resultados;
    }

    /**
     * Lista todos os segurados.
     */
    public List<Segurado> listarTodos() {
        return seguradoDAO.buscarTodos();
    }

    /**
     * Atualiza CPF de segurado.
     */
    public void atualizarCPF(int id, String novoCpf) throws SQLException {
        int idServidor = seguradoDAO.obterIdServidorPorSegurado(id);
        PermissaoValidator.validarPodeAlterarCpfNb(idServidor);

        try {
            seguradoDAO.atualizarCPF(id, novoCpf);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar CPF: verifique se o CPF já está em uso.", e);
        }
    }

    /**
     * Atualiza nome do segurado.
     */
    public void atualizarNome(int id, String novoNome) throws SQLException {
        int idServidor = seguradoDAO.obterIdServidorPorSegurado(id);
        PermissaoValidator.validarPodeAlterarNomeSegurado(idServidor);

        seguradoDAO.atualizarNome(id, novoNome);
    }

    /**
     * Exclui segurado pelo ID.
     */
    public void deletar(int id) throws SQLException {
        int idServidor = seguradoDAO.obterIdServidorPorSegurado(id);
        PermissaoValidator.validarPodeExcluir(idServidor);

        seguradoDAO.deletar(id);
    }

    /**
     * Busca segurado por CPF único.
     */
    public Segurado buscarPorCpfUnico(String cpf) {
        return seguradoDAO.buscarPorCpfUnico(cpf);
    }

    /**
     * Exclui segurado apenas se não tiver arquivos vinculados.
     */
    public void deletarSeNaoTiverArquivos(int id) throws SQLException {
        Segurado segurado = buscarPorId(id); // valida existência
        seguradoDAO.deletarSeNaoTiverArquivos(id);
    }
}
