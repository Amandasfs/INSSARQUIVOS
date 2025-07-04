package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.ArquivoDAO;
import inss.gca.mogi.mogi.dao.SeguradoDAO;
import inss.gca.mogi.mogi.dto.BuscaDTO;
import inss.gca.mogi.mogi.model.Arquivo;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Camada de serviço para manipulação dos dados de Arquivo.
 * Realiza validações de permissão e encapsula a lógica de negócio
 * entre o Controller e o DAO.
 */
public class ArquivoService {

    private final ArquivoDAO arquivoDAO;

    public ArquivoService() {
        this.arquivoDAO = new ArquivoDAO();
    }

    /**
     * Cria um novo arquivo.
     * @param arquivo objeto Arquivo com dados para inserção.
     * @throws DataIntegrityViolationException se ocorrer erro de integridade.
     */
    public void criar(Arquivo arquivo) {
        PermissaoValidator.validarPodeCadastrar(arquivo.getIdServidor());
        arquivoDAO.criar(arquivo);
    }

    /**
     * Atualiza os dados de um arquivo existente.
     * @param arquivo objeto Arquivo com dados atualizados.
     * @throws ObjectNotFoundException se o arquivo não existir.
     * @throws DataIntegrityViolationException para erros de integridade.
     */
    public void atualizar(Arquivo arquivo) {
        PermissaoValidator.validarPodeAlterar(arquivo.getIdServidor());

        try {
            arquivoDAO.atualizar(arquivo);
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("Arquivo não encontrado para atualização.");
        }
    }

    /**
     * Valida o perfil do usuário para determinada operação.
     * @param perfil perfil do usuário
     * @param perfisPermitidos perfis autorizados
     */
    private void validarPerfil(String perfil, String... perfisPermitidos) {
        for (String permitido : perfisPermitidos) {
            if (permitido.equalsIgnoreCase(perfil)) return;
        }
        throw new SecurityException("Acesso negado: perfil não autorizado.");
    }

    /**
     * Atualiza o número do benefício (NB) de um arquivo.
     * @param id identificador do arquivo.
     * @param novoNb novo número do benefício.
     * @throws ObjectNotFoundException se arquivo não encontrado.
     * @throws DataIntegrityViolationException para erros de integridade.
     */
    public void atualizarNb(int id, String novoNb) {
        int idServidor = arquivoDAO.obterIdServidorPorArquivo(id);
        PermissaoValidator.validarPodeAlterarCpfNb(idServidor);

        try {
            arquivoDAO.atualizarNb(id, novoNb);
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("Arquivo não encontrado para atualizar NB.");
        }
    }

    /**
     * Atualiza o código da caixa de um arquivo.
     * @param id identificador do arquivo.
     * @param novoCodCaixa novo código da caixa.
     * @throws ObjectNotFoundException se arquivo não encontrado.
     * @throws DataIntegrityViolationException para erros de integridade.
     */
    public void atualizarCaixa(int id, String novoCodCaixa) {
        int idServidor = arquivoDAO.obterIdServidorPorArquivo(id);
        PermissaoValidator.validarPodeAlterarCaixa(idServidor);

        try {
            arquivoDAO.atualizarCaixa(id, novoCodCaixa);
        } catch (ObjectNotFoundException e) {
            throw new ObjectNotFoundException("Arquivo não encontrado para atualizar caixa.");
        }
    }

    /**
     * Lista todos os arquivos cadastrados.
     * @return lista com todos os arquivos.
     */
    public List<BuscaDTO> buscarPorNB(String nb) {
        BuscaDTO dto = arquivoDAO.buscarPorNb(nb);
        return List.of(dto); // retornando lista com um único elemento (adaptar se quiser)
    }

    /**
     * Busca por NB com retorno do arquivo cadastrado ou caixa correspondente.
     * @param nb número do benefício.
     * @return DTO com informações do arquivo ou caixa.
     */
    public BuscaDTO buscarPorNbComVerificacao(String nb) {
        return arquivoDAO.buscarPorNb(nb);
    }


    /**
     * Exclui um arquivo e, caso o segurado não possua mais arquivos,
     * exclui também o segurado correspondente.
     */

    public void deletarArquivoSeguro(int idArquivo, int idServidorLogado) {
        // Valida permissão no DAO usando idServidorLogado
        arquivoDAO.deletar(idArquivo, idServidorLogado);
    }


    public void deletarArquivoPorNbSeguro(String nb, int idServidorLogado) {
        BuscaDTO dto = arquivoDAO.buscarPorNb(nb);

        if (dto == null || dto.getId() == 0) {
            throw new ObjectNotFoundException("Arquivo não encontrado para o NB informado: " + nb);
        }

        deletarArquivoSeguro(dto.getId(), idServidorLogado);
    }

}
