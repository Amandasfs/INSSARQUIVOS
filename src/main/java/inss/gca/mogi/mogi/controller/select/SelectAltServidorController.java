package inss.gca.mogi.mogi.controller.select;

import inss.gca.mogi.mogi.model.Servidor;
import inss.gca.mogi.mogi.service.ServidorService;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller responsável por permitir ao gerente buscar um servidor por matrícula
 * e abrir a tela de edição com os dados preenchidos.
 */
public class SelectAltServidorController {
    @FXML
    private TextField inputMatricula;

    private final ServidorService servidorService = new ServidorService();

    @FXML
    public void confirmar(ActionEvent event) {
        String texto = inputMatricula.getText().trim();
        if (texto.isEmpty()) {
            mostrarAlerta("Erro", "Digite uma matrícula para continuar.");
            return;
        }

        try {
            int matricula = Integer.parseInt(texto);
            Servidor servidor = servidorService.buscarPorMatricula(matricula);
            abrirTelaAlteracao(servidor);
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "A matrícula deve ser um número inteiro.");
        } catch (ObjectNotFoundException e) {
            mostrarAlerta("Servidor não encontrado", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao buscar servidor: " + e.getMessage());
        }
    }

    private void abrirTelaAlteracao(Servidor servidor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/update/AlterarServidor.fxml"));
            Parent root = loader.load();

            // Injeta o servidor no controller da tela de alteração
            inss.gca.mogi.mogi.controller.update.AlterarServidorController controller = loader.getController();
            controller.preencherCampos(servidor);

            Stage stage = new Stage();
            stage.setTitle("Alterar Servidor");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao abrir tela de alteração: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
