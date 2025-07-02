package inss.gca.mogi.mogi.security;

import inss.gca.mogi.mogi.dao.ServidorDAO;
import inss.gca.mogi.mogi.service.exceptions.PermissionDeniedException;

/**
 * Classe utilitária responsável por centralizar a validação de permissões de servidores.
 */
public class PermissaoValidator {

    private static final ServidorDAO servidorDAO = new ServidorDAO();

    public static void validarPodeCadastrar(int idServidor) {
        validar(servidorDAO.buscarPorId(idServidor).isPodeCadastrar(), "Servidor não possui permissão para cadastrar.");
    }

    public static void validarPodeAlterar(int idServidor) {
        validar(servidorDAO.buscarPorId(idServidor).isPodeAlterar(), "Servidor não possui permissão para alterar.");
    }

    public static void validarPodeAlterarNomeSegurado(int idServidor) {
        validar(servidorDAO.buscarPorId(idServidor).isPodeAlterarNomeSegurado(), "Servidor não possui permissão para alterar o nome do segurado.");
    }

    public static void validarPodeAlterarCaixa(int idServidor) {
        validar(servidorDAO.buscarPorId(idServidor).isPodeAlterarCaixa(), "Servidor não possui permissão para alterar a caixa.");
    }

    public static void validarPodeAlterarCpfNb(int idServidor) {
        validar(servidorDAO.buscarPorId(idServidor).isPodeAlterarCpfNb(), "Servidor não possui permissão para alterar CPF ou NB.");
    }

    public static void validarPodeAlterarLocalCaixa(int idServidor) {
        validar(servidorDAO.buscarPorId(idServidor).isPodeAlterarLocalCaixa(), "Servidor não possui permissão para alterar o local da caixa.");
    }

    public static void validarPodeAlterarCodigoCaixa(int idServidor) {
        validar(servidorDAO.buscarPorId(idServidor).isPodeAlterarCodigoCaixa(), "Servidor não possui permissão para alterar o código da caixa.");
    }

    public static void validarPodeExcluir(int idServidor) {
        validar(servidorDAO.buscarPorId(idServidor).isPodeExcluir(), "Servidor não possui permissão para excluir.");
    }

    public static void validarPerfilAtivo(int idServidor) {
        validar(servidorDAO.buscarPorId(idServidor).isStatusPerfil(), "O perfil do servidor está desativado.");
    }

    private static void validar(boolean condicao, String mensagemErro) {
        if (!condicao) {
            throw new PermissionDeniedException(mensagemErro);
        }
    }
}
