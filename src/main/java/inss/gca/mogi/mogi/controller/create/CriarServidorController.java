package inss.gca.mogi.mogi.controller.create;

import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.model.TipoPerfil;
import inss.gca.mogi.mogi.service.ServidorService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CriarSeguradoController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextField matriculaField;

    @FXML
    private PasswordField senhaField;

    @FXML
    private ChoiceBox<String> tipoSelect;

    @FXML
    private CheckBox cadastrarArquivo;
    @FXML
    private CheckBox alterarCod;
    @FXML
    private CheckBox alterarNome;
    @FXML
    private CheckBox alterarCaixa;
    @FXML
    private CheckBox alterarCpf;
    @FXML
    private CheckBox alterarLocal;
    @FXML
    private CheckBox excluir;

    private final ServidorService servidorService = new ServidorService();

    // ID do gerente logado — você pode setar dinamicamente depois
    private final int gerenteId = 1;

    @FXML
    public void cadastrar(ActionEvent event) {
        try {
            // Validações básicas
            if (nomeField.getText().isEmpty() || senhaField.getText().isEmpty() || matriculaField.getText().isEmpty()) {
                mostrarAlerta("Campos obrigatórios", "Preencha todos os campos obrigatórios.", Alert.AlertType.WARNING);
                return;
            }

            Servidor servidor = new Servidor();
            servidor.setNome(nomeField.getText());
            servidor.setSenha(senhaField.getText());
            servidor.setMatricula(Integer.parseInt(matriculaField.getText()));
            servidor.setTipoPerfil(mapearPerfil(tipoSelect.getValue()));
            servidor.setStatusPerfil(true);

            // Permissões
            servidor.setPodeCadastrar(cadastrarArquivo.isSelected());
            servidor.setPodeAlterar(alterarCpf.isSelected() || alterarNome.isSelected() || alterarCaixa.isSelected() || alterarCod.isSelected() || alterarLocal.isSelected());
            servidor.setPodeAlterarNomeSegurado(alterarNome.isSelected());
            servidor.setPodeAlterarCpfNb(alterarCpf.isSelected());
            servidor.setPodeAlterarCaixa(alterarCaixa.isSelected());
            servidor.setPodeAlterarCodigoCaixa(alterarCod.isSelected());
            servidor.setPodeAlterarLocalCaixa(alterarLocal.isSelected());
            servidor.setPodeExcluir(excluir.isSelected());

            servidorService.criarServidor(servidor, gerenteId);

            mostrarAlerta("Sucesso", "Servidor cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            limparCampos();

        } catch (Exception e) {
            e.printStackTrace(); // Exibe o erro no console
            mostrarAlerta("Erro ao cadastrar", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private TipoPerfil mapearPerfil(String value) {
        if (value == null) return TipoPerfil.ARQUIVISTA;
        if (value.equalsIgnoreCase("gerente")) return TipoPerfil.GERENTE;
        return TipoPerfil.ARQUIVISTA;
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void limparCampos() {
        nomeField.clear();
        senhaField.clear();
        matriculaField.clear();
        tipoSelect.setValue(null);
        cadastrarArquivo.setSelected(false);
        alterarCpf.setSelected(false);
        alterarNome.setSelected(false);
        alterarCaixa.setSelected(false);
        alterarCod.setSelected(false);
        alterarLocal.setSelected(false);
        excluir.setSelected(false);
    }
}
