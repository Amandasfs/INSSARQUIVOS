package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.CaixaDAO;
import inss.gca.mogi.mogi.dto.CaixaDTO;
import inss.gca.mogi.mogi.model.Caixa;
import inss.gca.mogi.mogi.model.TipoPerfil;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

import java.util.List;

/**
 * Camada de serviço que encapsula as regras de negócio relacionadas a Caixa,
 * como validação de permissões, sanitização de dados e operações seguras.
 */
public class CaixaService {

    // Instância do DAO para manipulação de dados
    private final CaixaDAO caixaDAO = new CaixaDAO();

    /**
     * Remove quaisquer caracteres não numéricos do número do benefício (NB).
     * Garante que apenas números sejam armazenados e processados.
     * @param nb número do benefício original (pode conter formatação)
     * @return número do benefício limpo (somente dígitos) ou null se nb for null
     */
    private String limparFormatoNb(String nb) {
        if (nb == null) return null;
        return nb.replaceAll("[^\\d]", "");
    }

    /**
     * Cadastra uma nova caixa após validar permissões e sanitizar dados.
     * @param caixa objeto Caixa a ser cadastrado
     * @param perfilUsuario perfil do usuário que está realizando a operação
     * @param idServidor id do servidor que realiza a operação
     * @throws SecurityException se perfil não autorizado
     */
    public void cadastrarCaixa(Caixa caixa, String perfilUsuario, int idServidor) {
        PermissaoValidator.validarPodeCadastrar(idServidor);
        validarPerfil(perfilUsuario, "GERENTE", "ARQUIVISTA_AUTORIZADO");

        // Remove formatação dos NBs antes de salvar
        caixa.setNbInicial(limparFormatoNb(caixa.getNbInicial()));
        caixa.setNbFinal(limparFormatoNb(caixa.getNbFinal()));

        caixaDAO.cadastrar(caixa);
    }

    /**
     * Retorna todas as caixas cadastradas.
     * @return lista de caixas
     */
    public List<Caixa> buscarTodasCaixas() {
        return caixaDAO.buscarTodas();
    }

    /**
     * Atualiza a localização de uma caixa (rua, prateleira, andar) após validações.
     * @param codCaixa código da caixa
     * @param rua nova rua
     * @param prateleira nova prateleira
     * @param andar novo andar
     * @param perfilUsuario perfil do usuário que executa a operação
     * @param idServidor id do servidor que executa a operação
     */
    public void atualizarLocal(String codCaixa, String rua, int prateleira, String andar, String perfilUsuario, int idServidor) {
        PermissaoValidator.validarPodeAlterarLocalCaixa(idServidor);
        validarPerfil(perfilUsuario, "GERENTE", "ARQUIVISTA_AUTORIZADO");

        caixaDAO.atualizarLocal(codCaixa, rua, prateleira, andar);
    }

    /**
     * Atualiza os números de benefício (NB) de uma caixa, com validação.
     * @param codCaixa código da caixa
     * @param nbInicial novo NB inicial (formatado ou não)
     * @param nbFinal novo NB final (formatado ou não)
     * @param perfilUsuario perfil do usuário
     * @param idServidor id do servidor
     */
    public void atualizarNb(String codCaixa, String nbInicial, String nbFinal, String perfilUsuario, int idServidor) {
        PermissaoValidator.validarPodeAlterarCpfNb(idServidor);
        validarPerfil(perfilUsuario, "GERENTE", "ARQUIVISTA_AUTORIZADO");

        nbInicial = limparFormatoNb(nbInicial);
        nbFinal = limparFormatoNb(nbFinal);

        caixaDAO.atualizarNb(codCaixa, nbInicial, nbFinal);
    }

    /**
     * Atualiza o código da caixa após validações.
     * @param codCaixaAntigo código atual da caixa
     * @param codCaixaNovo novo código para a caixa
     * @param perfilUsuario perfil do usuário
     * @param idServidor id do servidor
     */
    public void atualizarCodigoCaixa(String codCaixaAntigo, String codCaixaNovo, String perfilUsuario, int idServidor) {
        PermissaoValidator.validarPodeAlterarCodigoCaixa(idServidor);
        validarPerfil(perfilUsuario, "GERENTE", "ARQUIVISTA_AUTORIZADO");

        caixaDAO.atualizarCodigo(codCaixaAntigo, codCaixaNovo);
    }

    /**
     * Deleta a caixa somente se não houver arquivos vinculados.
     * Realiza validações de permissão e perfil.
     * @param codCaixa código da caixa
     * @param perfilUsuario perfil do usuário que executa
     * @throws RuntimeException se houver arquivos vinculados ou perfil não autorizado
     */
    public void deletarCaixaSeguro(String codCaixa, String perfilUsuario) {
        validarPerfil(perfilUsuario, "GERENTE");

        boolean temArquivos = caixaDAO.existeArquivoNaCaixa(codCaixa);
        if (temArquivos) {
            throw new RuntimeException("Não é possível excluir a caixa: existem arquivos vinculados.");
        }

        caixaDAO.deletar(codCaixa);
    }

    /**
     * Localiza uma caixa que contenha o NB informado.
     * @param nb número do benefício
     * @return objeto Caixa
     * @throws ObjectNotFoundException se não encontrada
     */
    public Caixa localizarCaixaPorNb(String nb) {
        Caixa caixa = caixaDAO.buscarCaixaPorNB(limparFormatoNb(nb));
        if (caixa == null) {
            throw new ObjectNotFoundException("Arquivo não encontrado ou arquivo não pertence a essa OL");
        }
        return caixa;
    }

    /**
     * Busca uma caixa pelo seu código.
     * @param codCaixa código da caixa
     * @return objeto Caixa encontrado
     * @throws ObjectNotFoundException se não encontrada
     */
    public Caixa buscaPorCodigoCaixa(String codCaixa) {
        return caixaDAO.buscarCaixaPorCodigo(codCaixa)
                .orElseThrow(() -> new ObjectNotFoundException("Caixa com código " + codCaixa + " não encontrada."));
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
     * Busca uma caixa e seus arquivos associados (números de benefício).
     * @param codCaixa código da caixa
     * @return DTO contendo dados da caixa e lista de NBs associados
     */
    public CaixaDTO buscarPorCodigoComArquivos(String codCaixa) {
        return caixaDAO.buscarPorCodigoComNbs(codCaixa);
    }
}
