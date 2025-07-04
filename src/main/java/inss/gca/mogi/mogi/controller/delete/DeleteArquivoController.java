package inss.gca.mogi.mogi.controller.delete;

import inss.gca.mogi.mogi.dto.BuscaDTO;
import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.service.ArquivoService;
import inss.gca.mogi.mogi.service.ServidorService;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;
import inss.gca.mogi.mogi.util.Sessao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.List;

public class DeleteArquivoController {

    @FXML
    private TextField matriculaField;

    @FXML
    private Button buscarButton;

    @FXML
    private Button excluirButton;

    @FXML
    private TableView<BuscaDTO> tabelaServidores;

    @FXML
    private TableColumn<BuscaDTO, String> nomeColumn;

    @FXML
    private TableColumn<BuscaDTO, String> caixaColumn;

    @FXML
    private ImageView closeButton;

    private ArquivoService arquivoService;
    private BuscaDTO arquivoSelecionado;

    @FXML
    public void initialize() {
        arquivoService = new ArquivoService();

        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nomeSegurado"));
        caixaColumn.setCellValueFactory(new PropertyValueFactory<>("codCaixa"));

        excluirButton.setDisable(true);

        buscarButton.setOnAction(event -> buscarArquivo());
        excluirButton.setOnAction(event -> excluirArquivo());
        closeButton.setOnMouseClicked(this::fecharJanela);
    }

    private void buscarArquivo() {
        String nb = matriculaField.getText().trim();

        if (nb.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Informe o número do arquivo (NB).");
            return;
        }

        try {
            List<BuscaDTO> resultados = arquivoService.buscarPorNB(nb);

            if (resultados.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Aviso", "Nenhum arquivo encontrado.");
                limparTabela();
                excluirButton.setDisable(true);
                return;
            }

            tabelaServidores.getItems().setAll(resultados);
            arquivoSelecionado = resultados.get(0);
            excluirButton.setDisable(false);

        } catch (ObjectNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
            limparTabela();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro inesperado", e.getMessage());
            limparTabela();
        }
    }

    private void excluirArquivo() {
        if (arquivoSelecionado == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Nenhum arquivo selecionado para exclusão.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação");
        confirm.setContentText("Deseja realmente excluir o arquivo NB: " + arquivoSelecionado.getNb() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Pega o servidor logado completo da sessão
                    Servidor servidorLogado = Sessao.getServidor();

                    if (servidorLogado == null) {
                        showAlert(Alert.AlertType.ERROR, "Erro", "Nenhum usuário está logado.");
                        return;
                    }

                    int matriculaLogado = servidorLogado.getMatricula();

                    arquivoService.deletarArquivoPorNbSeguro(arquivoSelecionado.getNb(), matriculaLogado);
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Arquivo excluído com sucesso.");
                    limparCamposETabela();

                } catch (ObjectNotFoundException e) {
                    showAlert(Alert.AlertType.ERROR, "Erro", e.getMessage());
                } catch (SecurityException e) {
                    showAlert(Alert.AlertType.ERROR, "Permissão", "Você não tem permissão para excluir este arquivo.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erro inesperado", e.getMessage());
                }
            }
        });
    }



    private void limparTabela() {
        tabelaServidores.getItems().clear();
        arquivoSelecionado = null;
    }

    private void limparCamposETabela() {
        matriculaField.clear();
        limparTabela();
        excluirButton.setDisable(true);
    }

    private void showAlert(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void fecharJanela(MouseEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
