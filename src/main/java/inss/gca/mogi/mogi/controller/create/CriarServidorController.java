package inss.gca.mogi.mogi.controller.create;

import inss.gca.mogi.mogi.dao.ServidorDAO;
import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.model.TipoPerfil;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.ServidorService;
import inss.gca.mogi.mogi.util.Sessao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CriarServidorController {

    // Campos do formulário
    @FXML private TextField nomeField;
    @FXML private TextField matriculaField;
    @FXML private PasswordField senhaField;
    @FXML private ChoiceBox<String> tipoSelect;

    // Checkboxes de permissões
    @FXML private CheckBox cadastrarArquivo;
    @FXML private CheckBox visualizarArquivo;
    @FXML private CheckBox alterarCod;
    @FXML private CheckBox alterarNome;
    @FXML private CheckBox alterarCaixa;
    @FXML private CheckBox alterarCpf;
    @FXML private CheckBox alterarLocal;
    @FXML private CheckBox excluir;
    @FXML private CheckBox gerenciarServidores;

    private final ServidorService servidorService;

    public CriarServidorController() {
        this.servidorService = new ServidorService(new ServidorDAO());
    }

    @FXML
    public void initialize() {
        // Configura as opções do ChoiceBox
        tipoSelect.getItems().addAll("Arquivista", "Gerente");
        tipoSelect.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            atualizarVisibilidadeCheckboxes(newVal);
        });
    }

    @FXML
    public void cadastrar(ActionEvent event) {
        try {
            // Valida permissão do usuário logado
            PermissaoValidator.validarPodeGerenciarServidores();

            if (!validarCampos()) {
                mostrarAlerta("Campos inválidos", "Preencha todos os campos obrigatórios corretamente.", Alert.AlertType.WARNING);
                return;
            }

            Servidor servidor = criarServidorFromForm();
            servidorService.criar(servidor, Sessao.getServidor().getId());

            mostrarAlerta("Sucesso", "Servidor cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            limparCampos();
        } catch (Exception e) {
            mostrarAlerta("Erro", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Servidor criarServidorFromForm() {
        Servidor servidor = new Servidor();
        servidor.setNome(nomeField.getText());
        servidor.setSenha(senhaField.getText()); // Na prática, armazene hash da senha
        servidor.setMatricula(matriculaField.getText());
        servidor.setTipoPerfil(TipoPerfil.fromString(tipoSelect.getValue()));
        servidor.setStatusPerfil(true);

        // Configura permissões
        if (servidor.getTipoPerfil() == TipoPerfil.GERENTE) {
            setTodasPermissoesGerente(servidor);
        } else {
            setPermissoesFromCheckboxes(servidor);
        }

        return servidor;
    }

    private void setTodasPermissoesGerente(Servidor servidor) {
        servidor.setPodeCadastrar(true);
        servidor.setPodeVisualizar(true);
        servidor.setPodeAlterar(true);
        servidor.setPodeAlterarNomeSegurado(true);
        servidor.setPodeAlterarCpfNb(true);
        servidor.setPodeAlterarCaixa(true);
        servidor.setPodeAlterarCodigoCaixa(true);
        servidor.setPodeAlterarLocalCaixa(true);
        servidor.setPodeExcluir(true);
        servidor.setPodeGerenciarServidores(true);
    }

    private void setPermissoesFromCheckboxes(Servidor servidor) {
        servidor.setPodeCadastrar(cadastrarArquivo.isSelected());
        servidor.setPodeVisualizar(visualizarArquivo.isSelected());
        servidor.setPodeAlterar(alterarCod.isSelected() || alterarNome.isSelected() ||
                alterarCaixa.isSelected() || alterarCpf.isSelected() ||
                alterarLocal.isSelected());
        servidor.setPodeAlterarNomeSegurado(alterarNome.isSelected());
        servidor.setPodeAlterarCpfNb(alterarCpf.isSelected());
        servidor.setPodeAlterarCaixa(alterarCaixa.isSelected());
        servidor.setPodeAlterarCodigoCaixa(alterarCod.isSelected());
        servidor.setPodeAlterarLocalCaixa(alterarLocal.isSelected());
        servidor.setPodeExcluir(excluir.isSelected());
        servidor.setPodeGerenciarServidores(gerenciarServidores.isSelected());
    }

    private void atualizarVisibilidadeCheckboxes(String tipoPerfil) {
        boolean isGerente = "Gerente".equalsIgnoreCase(tipoPerfil);

        // Desabilita checkboxes para gerente (todas permissões são true)
        cadastrarArquivo.setDisable(isGerente);
        visualizarArquivo.setDisable(isGerente);
        alterarCod.setDisable(isGerente);
        alterarNome.setDisable(isGerente);
        alterarCaixa.setDisable(isGerente);
        alterarCpf.setDisable(isGerente);
        alterarLocal.setDisable(isGerente);
        excluir.setDisable(isGerente);
        gerenciarServidores.setDisable(isGerente);

        // Marca/desmarca conforme perfil
        if (isGerente) {
            cadastrarArquivo.setSelected(true);
            visualizarArquivo.setSelected(true);
            alterarCod.setSelected(true);
            alterarNome.setSelected(true);
            alterarCaixa.setSelected(true);
            alterarCpf.setSelected(true);
            alterarLocal.setSelected(true);
            excluir.setSelected(true);
            gerenciarServidores.setSelected(true);
        }
    }

    private boolean validarCampos() {
        return !nomeField.getText().isEmpty() &&
                !matriculaField.getText().isEmpty() &&
                !senhaField.getText().isEmpty() &&
                tipoSelect.getValue() != null;
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
        matriculaField.clear();
        senhaField.clear();
        tipoSelect.getSelectionModel().clearSelection();
        cadastrarArquivo.setSelected(false);
        visualizarArquivo.setSelected(false);
        alterarCod.setSelected(false);
        alterarNome.setSelected(false);
        alterarCaixa.setSelected(false);
        alterarCpf.setSelected(false);
        alterarLocal.setSelected(false);
        excluir.setSelected(false);
        gerenciarServidores.setSelected(false);
    }
}