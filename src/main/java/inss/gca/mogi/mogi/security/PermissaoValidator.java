package inss.gca.mogi.mogi.security;

import inss.gca.mogi.mogi.dao.ServidorDAO;
import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.service.exceptions.AuthorizationException;
import inss.gca.mogi.mogi.util.Sessao;

/**
 * Classe utilitária para validação centralizada de permissões de servidores.
 */
public class PermissaoValidator {

    private static final ServidorDAO servidorDAO = new ServidorDAO();

    public static void validarPodeCadastrar() {
        Servidor servidor = Sessao.getServidor();
        if (servidor == null || (!servidor.isPodeCadastrar() && !servidor.isGerente())) {
            throw new AuthorizationException("Servidor não tem permissão para cadastrar.");
        }
    }

    public static void validarPodeAlterar(int matricula) {
        Servidor servidor = Sessao.getServidor();
        if (!servidor.isPodeAlterar() && !servidor.isGerente()) {
            throw new AuthorizationException("Servidor não tem permissão para alterar.");
        }
    }

    public static void validarPodeAlterarNomeSegurado(int matricula) {
        Servidor servidor = Sessao.getServidor();
        if (!servidor.isPodeAlterarNomeSegurado() && !servidor.isGerente()) {
            throw new AuthorizationException("Servidor não tem permissão para alterar nome do segurado.");
        }
    }

    public static void validarPodeAlterarCaixa(int matricula) {
        Servidor servidor = Sessao.getServidor();
        if (!servidor.isPodeAlterarCaixa() && !servidor.isGerente()) {
            throw new AuthorizationException("Servidor não tem permissão para alterar caixa.");
        }
    }

    public static void validarPodeAlterarCpfNb(int matricula) {
        Servidor servidor = Sessao.getServidor();
        if (!servidor.isPodeAlterarCpfNb() && !servidor.isGerente()) {
            throw new AuthorizationException("Servidor não tem permissão para alterar CPF/NB.");
        }
    }

    public static void validarPodeAlterarLocalCaixa(int matricula) {
        Servidor servidor = Sessao.getServidor();
        if (!servidor.isPodeAlterarLocalCaixa() && !servidor.isGerente()) {
            throw new AuthorizationException("Servidor não tem permissão para alterar local da caixa.");
        }
    }

    public static void validarPodeAlterarCodigoCaixa(int matricula) {
        Servidor servidor = Sessao.getServidor();
        if (!servidor.isPodeAlterarCodigoCaixa() && !servidor.isGerente()) {
            throw new AuthorizationException("Servidor não tem permissão para alterar código da caixa.");
        }
    }

    public static void validarPodeExcluir(int matricula) {
        Servidor servidor = Sessao.getServidor();
        if (!servidor.isPodeExcluir() && !servidor.isGerente()) {
            throw new AuthorizationException("Servidor não tem permissão para excluir.");
        }
    }

    public static void validarPerfilAtivo(int matricula) {
        Servidor servidor = Sessao.getServidor();
        if (!servidor.isStatusPerfil() && !servidor.isGerente()) {
            throw new AuthorizationException("Perfil do servidor está inativo.");
        }
    }

    /**
     * Valida se o servidor tem permissão para reativar perfis de outros servidores.
     * Apenas gerentes podem reativar perfis.
     *
     * @param matricula matrícula do servidor que está tentando reativar
     */
    public static void validarPodeReativarPerfil(int matricula) {
        Servidor servidor = Sessao.getServidor();
        if (!servidor.isGerente()) {
            throw new AuthorizationException("Somente gerentes podem reativar perfis de servidores.");
        }
    }
}
