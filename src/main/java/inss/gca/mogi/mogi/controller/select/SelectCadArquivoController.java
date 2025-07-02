package inss.gca.mogi.mogi.controller.select;

import inss.gca.mogi.mogi.dao.CaixaDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SelectCadArquivoController {

    @FXML
    private TextField codCaixaField;

    @FXML
    private TextField nbField;

    private final CaixaDAO caixaDAO = new CaixaDAO();

    @FXML
    public void adicionarArquivo(ActionEvent actionEvent) {
        String codCaixa = codCaixaField.getText();
        String nbDigitado = nbField.getText();

        if (codCaixa.isEmpty() || nbDigitado.isEmpty()) {
            showAlert("Campos obrigatórios", "Por favor, preencha o código da caixa e o NB.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Busca os limites da caixa
            String nbInicial = caixaDAO.buscarNbInicial(codCaixa);
            String nbFinal = caixaDAO.buscarNbFinal(codCaixa);

            if (nbInicial == null || nbFinal == null) {
                showAlert("Caixa não encontrada", "O código da caixa informado não existe.", Alert.AlertType.ERROR);
                return;
            }

            // Converte os NBs para inteiros puros para comparação
            long nbInt = Long.parseLong(nbDigitado.replaceAll("\\D", ""));
            long nbInicialInt = Long.parseLong(nbInicial.replaceAll("\\D", ""));
            long nbFinalInt = Long.parseLong(nbFinal.replaceAll("\\D", ""));

            if (nbInt >= nbInicialInt && nbInt <= nbFinalInt) {
                abrirTelaCadastroArquivo(codCaixa, nbDigitado);
            } else {
                showAlert("NB fora do intervalo", "O NB informado não pertence à faixa da caixa.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Formato de NB inválido", "Por favor, informe um NB válido (somente números e um dígito verificador).", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erro inesperado", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void abrirTelaCadastroArquivo(String codCaixa, String nb) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/create/CriarArquivo.fxml"));
            Parent root = loader.load();

            // Obter controller da tela de cadastro
            inss.gca.mogi.mogi.controller.create.CriarArquivoController controller = loader.getController();

            // Passar os dados para o controller
            controller.setDadosDaCaixaENb(codCaixa, nb);

            Stage stage = new Stage();
            stage.setTitle("CADASTRAR ARQUIVO");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro inesperado", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void showAlert(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
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
            showAlert("Erro inesperado", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
