package inss.gca.mogi.mogi.controller;

import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.service.LoginService;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField matriculaField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ImageView closeImageView;

    private final LoginService loginService = new LoginService();

    @FXML
    private void onEntrarClicked(ActionEvent event) {
        String matriculaStr = matriculaField.getText().trim();
        String senha = passwordField.getText();

        if (matriculaStr.isEmpty() || senha.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos obrigatórios", "Por favor, preencha a matrícula e a senha.");
            return;
        }

        try {
            int matricula = Integer.parseInt(matriculaStr);
            Servidor servidor = loginService.autenticar(matricula, senha);

            if (servidor != null) {
                switch (servidor.getTipoPerfil()) {
                    case GERENTE:
                        switchScene("/profile/Gerente.fxml");
                        break;
                    case ARQUIVISTA:
                        switchScene("/profile/Arquivista.fxml");
                        break;
                    default:
                        showAlert(Alert.AlertType.ERROR, "Perfil inválido", "Tipo de perfil não reconhecido.");
                }
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de formato", "A matrícula deve conter apenas números.");
        } catch (ObjectNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Falha no login", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            showAlert(Alert.AlertType.ERROR, "Erro interno", "Erro ao acessar o sistema. Contate o suporte.");
        }
    }

    @FXML
    private void onConsultarClicked(ActionEvent event) {
        switchScene("/profile/Consultor.fxml");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeImageView.getScene().getWindow();
        stage.close();
    }

    private void switchScene(String fxmlPath) {
        try {
            Stage stage = (Stage) matriculaField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene newScene = new Scene(loader.load());
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro ao carregar a tela", "Não foi possível abrir a nova tela.");
        }
    }

    private void showAlert(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
