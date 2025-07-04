package inss.gca.mogi.mogi.security;

import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.service.exceptions.AuthorizationException;
import inss.gca.mogi.mogi.util.Sessao;

/**
 * Classe utilitária para validação centralizada de permissões de servidores.
 * Versão otimizada para trabalhar com o novo sistema de permissões.
 */
public class PermissaoValidator {

    public static void validarPodeCadastrar() {
        validarAcao("PodeCadastrar");
    }

    public static void validarPodeAlterar() {
        validarAcao("PodeAlterar");
    }

    public static void validarPodeAlterarNomeSegurado() {
        validarAcao("PodeAlterarNomeSegurado");
    }

    public static void validarPodeAlterarCaixa() {
        validarAcao("PodeAlterarCaixa");
    }

    public static void validarPodeAlterarCpfNb() {
        validarAcao("PodeAlterarCpfNb");
    }

    public static void validarPodeAlterarLocalCaixa() {
        validarAcao("PodeAlterarLocalCaixa");
    }

    public static void validarPodeAlterarCodigoCaixa() {
        validarAcao("PodeAlterarCodigoCaixa");
    }

    public static void validarPodeExcluir() {
        validarAcao("PodeExcluir");
    }

    public static void validarPerfilAtivo() {
        Servidor servidor = Sessao.getServidor();
        if (servidor == null) {
            throw new AuthorizationException("Nenhum usuário autenticado.");
        }
        if (!servidor.isStatusPerfil() && !servidor.isGerente()) {
            throw new AuthorizationException("Perfil do servidor está inativo.");
        }
    }

    public static void validarPodeReativarPerfil() {
        validarGerente();
    }

    /**
     * Valida se o servidor tem permissão para uma ação específica
     * @param acao Nome da ação (deve corresponder ao nome do método isPodeXxx no Servidor)
     */
    public static void validarAcao(String acao) {
        Servidor servidor = Sessao.getServidor();
        if (servidor == null) {
            throw new AuthorizationException("Nenhum usuário autenticado.");
        }
        if (!servidor.temPermissao(acao)) {
            throw new AuthorizationException("Acesso negado para: " + acao);
        }
    }

    /**
     * Valida se o usuário atual é gerente
     */
    public static void validarGerente() {
        Servidor servidor = Sessao.getServidor();
        if (servidor == null || !servidor.isGerente()) {
            throw new AuthorizationException("Apenas gerentes podem executar esta ação.");
        }
    }

    /**
     * Valida se o usuário tem permissão para a ação OU é gerente
     */
    public static void validarAcaoOuGerente(String acao) {
        Servidor servidor = Sessao.getServidor();
        if (servidor == null) {
            throw new AuthorizationException("Nenhum usuário autenticado.");
        }
        if (!servidor.isGerente() && !servidor.temPermissao(acao)) {
            throw new AuthorizationException("Acesso negado para: " + acao);
        }
    }
}