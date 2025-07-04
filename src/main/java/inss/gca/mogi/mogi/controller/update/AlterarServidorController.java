package inss.gca.mogi.mogi.controller.update;

import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.model.TipoPerfil;
import inss.gca.mogi.mogi.service.ServidorService;
import inss.gca.mogi.mogi.util.Sessao;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller responsável pela tela de alteração de dados de um servidor.
 * Permite editar dados básicos e permissões.
 */
public class AlterarServidorController {

    @FXML private TextField nomeField;
    @FXML private TextField matriculaField;
    @FXML private PasswordField senhaField;
    @FXML private ChoiceBox<String> tipoSelect;
    @FXML private ChoiceBox<String> statusSelect;

    @FXML private CheckBox cadastrarArquivo;
    @FXML private CheckBox alterarCpf;
    @FXML private CheckBox alterarLocal;
    @FXML private CheckBox alterarCod;
    @FXML private CheckBox alterarArquivo;
    @FXML private CheckBox alterarNome;
    @FXML private CheckBox alterarCaixa;
    @FXML private CheckBox excluir;

    private Servidor servidorAtual;

    private final ServidorService servidorService = new ServidorService();

    // 🔧 Simulação de gerente logado
    private final int idServidorLogado = 1;

    /**
     * Método chamado para preencher os campos da tela com os dados do servidor.
     */
    public void preencherCampos(Servidor servidor) {
        this.servidorAtual = servidor;

        nomeField.setText(servidor.getNome());
        matriculaField.setText(String.valueOf(servidor.getMatricula()));
        tipoSelect.setValue(servidor.getTipoPerfil().toString());
        statusSelect.setValue(servidor.isStatusPerfil() ? "ATIVO" : "INATIVO");

        cadastrarArquivo.setSelected(servidor.isPodeCadastrar());
        alterarCpf.setSelected(servidor.isPodeAlterarCpfNb());
        alterarLocal.setSelected(servidor.isPodeAlterarLocalCaixa());
        alterarCod.setSelected(servidor.isPodeAlterarCodigoCaixa());
        alterarArquivo.setSelected(servidor.isPodeAlterar());
        alterarNome.setSelected(servidor.isPodeAlterarNomeSegurado());
        alterarCaixa.setSelected(servidor.isPodeAlterarCaixa());
        excluir.setSelected(servidor.isPodeExcluir());
    }

    /**
     * Método chamado ao clicar no botão "ALTERAR".
     * Atualiza o objeto servidor com os dados da tela e persiste as alterações.
     */
    @FXML
    private void alterar() {
        try {
            boolean statusAnterior = servidorAtual.isStatusPerfil();
            boolean statusAtual = "ATIVO".equals(statusSelect.getValue());

            servidorAtual.setNome(nomeField.getText());
            servidorAtual.setMatricula(Integer.parseInt(matriculaField.getText()));
            servidorAtual.setTipoPerfil(TipoPerfil.valueOf(tipoSelect.getValue()));

            servidorAtual.setPodeCadastrar(cadastrarArquivo.isSelected());
            servidorAtual.setPodeAlterar(alterarArquivo.isSelected());
            servidorAtual.setPodeAlterarNomeSegurado(alterarNome.isSelected());
            servidorAtual.setPodeAlterarCaixa(alterarCaixa.isSelected());
            servidorAtual.setPodeAlterarCpfNb(alterarCpf.isSelected());
            servidorAtual.setPodeAlterarLocalCaixa(alterarLocal.isSelected());
            servidorAtual.setPodeAlterarCodigoCaixa(alterarCod.isSelected());
            servidorAtual.setPodeExcluir(excluir.isSelected());

            if (!statusAnterior && statusAtual) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Confirmação de Reativação");
                confirm.setHeaderText("O servidor está inativo.");
                confirm.setContentText("Deseja reativar este servidor?");

                var result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    servidorAtual.setStatusPerfil(true);
                } else {
                    servidorAtual.setStatusPerfil(false);
                    showAlert("Aviso", "O servidor permanece inativo.");
                    return;
                }
            } else {
                servidorAtual.setStatusPerfil(statusAtual);
            }

            if (!senhaField.getText().trim().isEmpty()) {
                servidorService.atualizarSenha(servidorAtual.getId(), senhaField.getText().trim());
            }

            // 🎯 Tentativa real de pegar o servidor logado da sessão
            Servidor gerenteLogado = Sessao.getServidor();

            // 🔧 Simulação se a sessão estiver nula
            if (gerenteLogado == null) {
                gerenteLogado = servidorService.buscarPorId(idServidorLogado);
                System.out.println("⚠️ Sessão nula — usando simulação de gerente com ID " + idServidorLogado);
            }

            if (gerenteLogado == null || !gerenteLogado.isGerente()) {
                showAlert("Erro", "Apenas gerentes podem alterar dados de outros servidores.");
                return;
            }

            int matriculaGerente = gerenteLogado.getMatricula();

            servidorService.atualizarServidor(servidorAtual, matriculaGerente);
            servidorService.atualizarPermissoes(servidorAtual, matriculaGerente);

            showAlert("Sucesso", "Servidor atualizado com sucesso.");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao atualizar servidor: " + e.getMessage());
        }
    }

    /**
     * Mostra um alerta informativo para o usuário.
     */
    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
