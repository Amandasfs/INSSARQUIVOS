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
 * Serviço responsável por conter as regras de negócio e
 * orquestrar operações com a entidade Segurado.
 */
public class SeguradoService {

    private final SeguradoDAO seguradoDAO;

    public SeguradoService() {
        this.seguradoDAO = new SeguradoDAO();
    }

    public Segurado criarSegurado(Segurado segurado) {
        PermissaoValidator.validarPodeCadastrar(segurado.getIdServidor());

        try {
            seguradoDAO.criar(segurado);
            return segurado;
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao criar segurado: dados inválidos ou CPF já existente.", e);
        }
    }

    public Segurado buscarPorId(int id) {
        return seguradoDAO.buscarPorId(id);
    }

    public List<BuscaDTO> buscarPorCpf(String cpf) {
        List<BuscaDTO> segurado = seguradoDAO.buscarPorCpf(cpf);
        if (segurado.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum arquivo encontrado para o CPF: " + cpf);
        }
        return segurado;
    }

    public List<Segurado> listarTodos() {
        return seguradoDAO.buscarTodos();
    }

    public void atualizarCPF(int id, String novoCpf) throws SQLException {
        int idServidor = seguradoDAO.obterIdServidorPorSegurado(id);
        PermissaoValidator.validarPodeAlterarCpfNb(idServidor);

        try {
            seguradoDAO.atualizarCPF(id, novoCpf);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro ao atualizar CPF: verifique se o CPF já está em uso.", e);
        }
    }

    public void atualizarNome(int id, String novoNome) throws SQLException {
        int idServidor = seguradoDAO.obterIdServidorPorSegurado(id);
        PermissaoValidator.validarPodeAlterarNomeSegurado(idServidor);

        seguradoDAO.atualizarNome(id, novoNome);
    }

    public void deletar(int id) throws SQLException {
        int idServidor = seguradoDAO.obterIdServidorPorSegurado(id);
        PermissaoValidator.validarPodeExcluir(idServidor);

        seguradoDAO.deletar(id);
    }

    public Segurado buscarPorCpfUnico(String cpf) {
        return seguradoDAO.buscarPorCpfUnico(cpf);
    }
}
