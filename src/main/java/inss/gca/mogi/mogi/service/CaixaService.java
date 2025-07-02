package inss.gca.mogi.mogi.service;

import inss.gca.mogi.mogi.dao.CaixaDAO;
import inss.gca.mogi.mogi.dto.CaixaDTO;
import inss.gca.mogi.mogi.model.Caixa;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;

import java.util.List;

/**
 * Camada de serviço para regras de negócio relacionadas a Caixa.
 */
public class CaixaService {

    private final CaixaDAO caixaDAO = new CaixaDAO();

    /**
     * Remove qualquer formatação do NB antes de salvar no banco.
     */
    private String limparFormatoNb(String nb) {
        if (nb == null) return null;
        return nb.replaceAll("[^\\d]", "");
    }

    public void cadastrarCaixa(Caixa caixa, String perfilUsuario, int idServidor) {
        // Valida permissão de cadastro via PermissaoValidator
        PermissaoValidator.validarPodeCadastrar(idServidor);

        // Valida perfil
        validarPerfil(perfilUsuario, "GERENTE", "ARQUIVISTA_AUTORIZADO");

        // Sanitiza NB antes de enviar ao DAO
        caixa.setNbInicial(limparFormatoNb(caixa.getNbInicial()));
        caixa.setNbFinal(limparFormatoNb(caixa.getNbFinal()));

        caixaDAO.cadastrar(caixa);
    }

    public List<Caixa> buscarTodasCaixas() {
        return caixaDAO.buscarTodas();
    }

    public void atualizarLocal(String codCaixa, String rua, int prateleira, String andar, String perfilUsuario, int idServidor) {
        // Valida permissão de alteração do local da caixa
        PermissaoValidator.validarPodeAlterarLocalCaixa(idServidor);

        // Valida perfil
        validarPerfil(perfilUsuario, "GERENTE", "ARQUIVISTA_AUTORIZADO");

        caixaDAO.atualizarLocal(codCaixa, rua, prateleira, andar);
    }

    public void atualizarNb(String codCaixa, String nbInicial, String nbFinal, String perfilUsuario, int idServidor) {
        // Valida permissão para alterar CPF/NB
        PermissaoValidator.validarPodeAlterarCpfNb(idServidor);

        // Valida perfil
        validarPerfil(perfilUsuario, "GERENTE", "ARQUIVISTA_AUTORIZADO");

        nbInicial = limparFormatoNb(nbInicial);
        nbFinal = limparFormatoNb(nbFinal);

        caixaDAO.atualizarNb(codCaixa, nbInicial, nbFinal);
    }

    public void atualizarCodigoCaixa(String codCaixaAntigo, String codCaixaNovo, String perfilUsuario, int idServidor) {
        // Valida permissão para alterar código da caixa
        PermissaoValidator.validarPodeAlterarCodigoCaixa(idServidor);

        // Valida perfil
        validarPerfil(perfilUsuario, "GERENTE", "ARQUIVISTA_AUTORIZADO");

        caixaDAO.atualizarCodigo(codCaixaAntigo, codCaixaNovo);
    }

    public void deletarCaixa(String codCaixa, String perfilUsuario, int idServidor) {
        // Valida permissão para excluir
        PermissaoValidator.validarPodeExcluir(idServidor);

        // Valida perfil
        validarPerfil(perfilUsuario, "GERENTE");

        caixaDAO.deletar(codCaixa);
    }

    public Caixa localizarCaixaPorNb(String nb) {
        Caixa caixa = caixaDAO.buscarCaixaPorNB(limparFormatoNb(nb));
        if (caixa == null) {
            throw new ObjectNotFoundException("Arquivo não encontrado ou arquivo não pertence a essa OL");
        }
        return caixa;
    }

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

    public CaixaDTO buscarPorCodigoComArquivos(String codCaixa) {
        return caixaDAO.buscarPorCodigoComNbs(codCaixa);
    }
}
