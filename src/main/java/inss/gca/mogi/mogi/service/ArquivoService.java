package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.ArquivoDAO;
import inss.gca.mogi.mogi.dto.BuscaDTO;
import inss.gca.mogi.mogi.model.Arquivo;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Camada de serviço responsável por aplicar regras de negócio
 * e intermediar o acesso entre o controller e o DAO.
 */
public class ArquivoService {

    private final ArquivoDAO arquivoDAO;

    public ArquivoService() {
        this.arquivoDAO = new ArquivoDAO();
    }

    public void criar(Arquivo arquivo) {
        // Validação de permissão para criar
        PermissaoValidator.validarPodeCadastrar(arquivo.getIdServidor());

        try {
            arquivoDAO.criar(arquivo);
        } catch (DataIntegrityViolationException e) {
            throw e;
        }
    }

    public void atualizar(Arquivo arquivo) {
        PermissaoValidator.validarPodeAlterar(arquivo.getIdServidor());

        try {
            arquivoDAO.atualizar(arquivo);
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("Arquivo não encontrado para atualização.");
        } catch (DataIntegrityViolationException e) {
            throw e;
        }
    }

    public void deletar(int id) {
        int idServidor = arquivoDAO.obterIdServidorPorArquivo(id);
        PermissaoValidator.validarPodeExcluir(idServidor);

        try {
            arquivoDAO.deletar(id);
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("Arquivo não encontrado para exclusão.");
        } catch (DataIntegrityViolationException e) {
            throw e;
        }
    }

    public Arquivo buscarPorId(int id) {
        try {
            return arquivoDAO.buscarPorId(id);
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("Arquivo não encontrado.");
        }
    }

    public List<BuscaDTO> buscarPorNB(String nb) {
        try {
            BuscaDTO resultado = arquivoDAO.buscarPorNb(nb);
            List<BuscaDTO> lista = new ArrayList<>();
            lista.add(resultado);
            return lista;
        } catch (RuntimeException e) {
            throw new ObjectNotFoundException("Arquivo não encontrado para NB: " + nb, e);
        }
    }

    public void atualizarNb(int id, String novoNb) {
        int idServidor = arquivoDAO.obterIdServidorPorArquivo(id);
        PermissaoValidator.validarPodeAlterarCpfNb(idServidor);

        try {
            arquivoDAO.atualizarNb(id, novoNb);
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("Arquivo não encontrado para atualizar NB.");
        } catch (DataIntegrityViolationException e) {
            throw e;
        }
    }

    public void atualizarCaixa(int id, String novoCodCaixa) {
        int idServidor = arquivoDAO.obterIdServidorPorArquivo(id);
        PermissaoValidator.validarPodeAlterarCaixa(idServidor);

        try {
            arquivoDAO.atualizarCaixa(id, novoCodCaixa);
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("Arquivo não encontrado para atualizar caixa.");
        } catch (DataIntegrityViolationException e) {
            throw e;
        }
    }

    public List<Arquivo> listarTodos() {
        return arquivoDAO.listarTodos();
    }
    /**
     * Busca por NB, retorna arquivo cadastrado ou caixa onde o NB está se arquivo não cadastrado.
     */
    public BuscaDTO buscarPorNbComVerificacao(String nb) {
        return arquivoDAO.buscarPorNb(nb);
    }
}
