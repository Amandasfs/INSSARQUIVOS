package inss.gca.mogi.mogi.controller.update;

import inss.gca.mogi.mogi.config.PasswordUtil;
import inss.gca.mogi.mogi.model.LogServidor;
import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.service.LogServidorService;
import inss.gca.mogi.mogi.service.ServidorService;
import inss.gca.mogi.mogi.util.AlertaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

import java.time.LocalDateTime;

/**
 * Controller responsável por gerenciar a troca de senha de um servidor.
 */
public class AlterarSenhaController {

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField newpassword;

    @FXML
    private PasswordField repeatpassword;

    private final ServidorService servidorService = new ServidorService();
    private final LogServidorService logService = new LogServidorService();

    // Simulação: ID do servidor logado (substitua por um sistema de sessão real se tiver)
    private int servidorLogadoId = 1;

    @FXML
    public void onTrocar(ActionEvent actionEvent) {
        try {
            String senhaAtual = password.getText();
            String novaSenha = newpassword.getText();
            String confirmarSenha = repeatpassword.getText();

            // Validação básica dos campos
            if (senhaAtual.isEmpty() || novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
                AlertaUtil.exibirErro("Preencha todos os campos.");
                return;
            }

            // Verificar se a nova senha e a confirmação coincidem
            if (!novaSenha.equals(confirmarSenha)) {
                AlertaUtil.exibirErro("A nova senha e a confirmação não coincidem.");
                return;
            }

            // Buscar o servidor no banco
            Servidor servidor = servidorService.buscarPorId(servidorLogadoId);
            if (servidor == null) {
                AlertaUtil.exibirErro("Servidor não encontrado.");
                return;
            }

            // Verificar se a senha atual está correta
            if (!PasswordUtil.verifyPassword(senhaAtual, servidor.getSenha())) {
                AlertaUtil.exibirErro("Senha atual incorreta.");
                return;
            }

            // *** NÃO faça hash aqui! Apenas passe a senha pura para o serviço atualizar ***
            servidorService.atualizarSenha(servidorLogadoId, novaSenha);

            // Registrar log
            LogServidor log = new LogServidor();
            log.setServidorId(servidorLogadoId);
            log.setAcao("ALTERAÇÃO DE SENHA");
            log.setDataHora(LocalDateTime.now());
            log.setDetalhes("Servidor alterou sua própria senha.");

            logService.registrarAcao(log);

            // Sucesso
            AlertaUtil.exibirInformacao("Senha alterada com sucesso!");

            // Limpar campos
            password.clear();
            newpassword.clear();
            repeatpassword.clear();

        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.exibirErro("Erro ao alterar senha: " + e.getMessage());
        }
    }

}
