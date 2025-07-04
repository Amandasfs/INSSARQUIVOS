package inss.gca.mogi.mogi.controller.profile;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class PainelServidorController {

    @FXML
    private ImageView gerarRelatorio;

    @FXML
    private ImageView editar; // editar

    @FXML
    public void initialize() {
        gerarRelatorio.setOnMouseClicked(event -> abrirTela("/util/RelatorioServidor.fxml", "Relatório de Servidores"));
        editar.setOnMouseClicked(event -> abrirTela("/select/SelectAltServidor.fxml", "Editar Servidor"));
    }

    private void abrirTela(String path, String titulo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("ERRO", "Erro ao abrir tela: " + e.getMessage());
        }
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
