package inss.gca.mogi.mogi.controller.update;

import inss.gca.mogi.mogi.dao.LogServidorDAO;
import inss.gca.mogi.mogi.model.Caixa;
import inss.gca.mogi.mogi.model.LogServidor;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.CaixaService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.time.LocalDateTime;

public class AlterarCaixaController {

    @FXML
    private TextField codCaixaField;
    @FXML
    private TextField ruaField;
    @FXML
    private TextField prateleiraField;
    @FXML
    private TextField nbInicialField;
    @FXML
    private TextField nbFinalField;
    @FXML
    private ChoiceBox<String> andarSelect;
    @FXML
    private Button cadastrarButton;

    private Caixa caixaOriginal;
    private int idServidor;
    private String perfilServidor;

    private final CaixaService caixaService = new CaixaService();
    private final LogServidorDAO logDAO = new LogServidorDAO();

    public void initData(Caixa caixa, int idServidor, String perfilServidor) {
        this.caixaOriginal = caixa;
        this.idServidor = idServidor;
        this.perfilServidor = perfilServidor;

        preencherCampos(caixa);
        aplicarPermissoes();
    }

    private void preencherCampos(Caixa caixa) {
        codCaixaField.setText(caixa.getCodCaixa());
        ruaField.setText(caixa.getRua());
        prateleiraField.setText(String.valueOf(caixa.getPrateleira()));
        nbInicialField.setText(caixa.getNbInicial());
        nbFinalField.setText(caixa.getNbFinal());
        andarSelect.setValue(caixa.getAndar());
    }

    private void aplicarPermissoes() {
        try {
            PermissaoValidator.validarPodeAlterarCodigoCaixa(idServidor);
        } catch (Exception e) {
            codCaixaField.setDisable(true);
        }

        try {
            PermissaoValidator.validarPodeAlterarLocalCaixa(idServidor);
        } catch (Exception e) {
            ruaField.setDisable(true);
            prateleiraField.setDisable(true);
            andarSelect.setDisable(true);
        }

        try {
            PermissaoValidator.validarPodeAlterarCpfNb(idServidor);
        } catch (Exception e) {
            nbInicialField.setDisable(true);
            nbFinalField.setDisable(true);
        }

        cadastrarButton.setText("SALVAR ALTERAÇÕES");
    }

    @FXML
    private void salvarAlteracoes(ActionEvent event) {
        try {
            // Verifica e aplica alterações de código
            if (!codCaixaField.isDisabled() && !codCaixaField.getText().equals(caixaOriginal.getCodCaixa())) {
                caixaService.atualizarCodigoCaixa(caixaOriginal.getCodCaixa(), codCaixaField.getText(), perfilServidor, idServidor);
                registrarLog("Alteração de código da caixa", "De: " + caixaOriginal.getCodCaixa() + " Para: " + codCaixaField.getText());
            }

            // Verifica e aplica alterações de local
            if (!ruaField.isDisabled() || !prateleiraField.isDisabled() || !andarSelect.isDisabled()) {
                String novaRua = ruaField.getText();
                int novaPrateleira = Integer.parseInt(prateleiraField.getText());
                String novoAndar = andarSelect.getValue();

                if (!novaRua.equals(caixaOriginal.getRua()) ||
                        novaPrateleira != caixaOriginal.getPrateleira() ||
                        !novoAndar.equals(caixaOriginal.getAndar())) {

                    caixaService.atualizarLocal(caixaOriginal.getCodCaixa(), novaRua, novaPrateleira, novoAndar, perfilServidor, idServidor);
                    registrarLog("Alteração de local da caixa", "Rua: " + novaRua + ", Prateleira: " + novaPrateleira + ", Andar: " + novoAndar);
                }
            }

            // Verifica e aplica alterações de NB
            if (!nbInicialField.isDisabled() || !nbFinalField.isDisabled()) {
                String novoNbInicial = nbInicialField.getText();
                String novoNbFinal = nbFinalField.getText();

                if (!novoNbInicial.equals(caixaOriginal.getNbInicial()) ||
                        !novoNbFinal.equals(caixaOriginal.getNbFinal())) {

                    caixaService.atualizarNb(caixaOriginal.getCodCaixa(), novoNbInicial, novoNbFinal, perfilServidor, idServidor);
                    registrarLog("Alteração de NB da caixa", "De: " + caixaOriginal.getNbInicial() + "-" + caixaOriginal.getNbFinal() +
                            " Para: " + novoNbInicial + "-" + novoNbFinal);
                }
            }

            exibirAlerta("Sucesso", "Alterações salvas com sucesso.");

            // Fecha a janela
            cadastrarButton.getScene().getWindow().hide();

        } catch (Exception e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao salvar alterações: " + e.getMessage());
        }
    }

    private void registrarLog(String acao, String detalhes) {
        LogServidor log = new LogServidor();
        log.setServidorId(idServidor);
        log.setAcao(acao);
        log.setDataHora(LocalDateTime.now());
        log.setDetalhes("Caixa: " + caixaOriginal.getCodCaixa() + " | " + detalhes);

        logDAO.registrarLog(log);
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
