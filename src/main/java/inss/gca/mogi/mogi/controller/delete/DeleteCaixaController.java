package inss.gca.mogi.mogi.controller.delete;

import inss.gca.mogi.mogi.dto.CaixaDTO;
import inss.gca.mogi.mogi.service.CaixaService;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DeleteCaixaController {

    @FXML
    private TextField codigoField;

    @FXML
    private Button buscarButton;

    @FXML
    private Button excluirButton;

    @FXML
    private TableView<CaixaDTO> tabelaServidores;

    @FXML
    private TableColumn<CaixaDTO, String> nomeColumn;

    @FXML
    private TableColumn<CaixaDTO, String> matriculaColumn;

    @FXML
    private ImageView closeButton;

    private final CaixaService caixaService = new CaixaService();

    // Simulando servidor logado
    private final int idServidorLogado = 1;
    private final String perfilLogado = "GERENTE";

    @FXML
    public void initialize() {
        // Configura colunas da tabela
        nomeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCodCaixa())
        );

        matriculaColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(cellData.getValue().getNbs().size())
                )
        );
        // Botão fechar janela
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        // Ação buscar
        buscarButton.setOnAction(e -> buscarCaixa());

        // Ação excluir
        excluirButton.setOnAction(e -> excluirCaixa());
    }

    /**
     * Busca uma caixa pelo código digitado e exibe os dados na tabela.
     */
    private void buscarCaixa() {
        String codigo = codigoField.getText().trim();

        if (codigo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Informe o código da caixa.");
            return;
        }

        try {
            CaixaDTO caixaDTO = caixaService.buscarPorCodigoComArquivos(codigo);
            tabelaServidores.getItems().clear();
            tabelaServidores.getItems().add(caixaDTO);
        } catch (ObjectNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Caixa não encontrada.");
            tabelaServidores.getItems().clear();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao buscar caixa: " + e.getMessage());
        }
    }

    /**
     * Realiza exclusão segura da caixa, validando perfil e se há arquivos vinculados.
     */
    private void excluirCaixa() {
        String codigo = codigoField.getText().trim();

        if (codigo.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Informe o código da caixa.");
            return;
        }

        try {
            caixaService.deletarCaixaSeguro(codigo, perfilLogado);
            showAlert(Alert.AlertType.INFORMATION, "Caixa excluída com sucesso.");
            tabelaServidores.getItems().clear();
            codigoField.clear();
        } catch (ObjectNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Caixa não encontrada.");
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.WARNING, e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro ao excluir caixa: " + e.getMessage());
        }
    }

    /**
     * Exibe uma caixa de alerta para o usuário.
     * @param alertType tipo de alerta
     * @param mensagem texto a ser exibido
     */
    private void showAlert(Alert.AlertType alertType, String mensagem) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Excluir Caixa");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
