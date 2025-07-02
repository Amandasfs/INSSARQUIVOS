package inss.gca.mogi.mogi.controller.select;

import inss.gca.mogi.mogi.dto.BuscaDTO;
import inss.gca.mogi.mogi.service.ArquivoService;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.controller.update.AlterarArquivoController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SelectAltArquivoController {

    @FXML
    private TextField numArquivo;

    private final ArquivoService arquivoService = new ArquivoService();

    // TODO: Substituir por dados reais do servidor logado
    private final int idServidor = 1;
    private final String perfilServidor = "admin";

    @FXML
    private void adicionarArquivo(ActionEvent event) {
        String input = numArquivo.getText().trim();

        if (input.isEmpty()) {
            showAlert("Erro", "Por favor, digite o número do benefício.");
            return;
        }

        // Limpa NB: remove tudo que não for número
        String nbLimpo = input.replaceAll("[^0-9]", "");

        try {
            List<BuscaDTO> resultados = arquivoService.buscarPorNB(nbLimpo);

            if (resultados == null || resultados.isEmpty()) {
                showAlert("Erro", "Nenhum arquivo encontrado com esse NB.");
                return;
            }

            BuscaDTO resultado = resultados.get(0);

            // Verificação de permissão do servidor
            PermissaoValidator.validarPerfilAtivo(idServidor);
            PermissaoValidator.validarPodeAlterar(idServidor);

            abrirTelaAlteracao(resultado);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao buscar o arquivo. Verifique o número do benefício ou suas permissões.");
        }
    }

    private void abrirTelaAlteracao(BuscaDTO arquivo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/update/AlterarArquivo.fxml"));
            Parent root = loader.load();

            AlterarArquivoController controller = loader.getController();
            controller.initDataBuscaDTO(arquivo, idServidor, perfilServidor);

            Stage stage = new Stage();
            stage.setTitle("Alterar Arquivo");
            stage.setScene(new Scene(root));
            stage.show();

            // Fecha janela atual (de seleção)
            numArquivo.getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao abrir a tela de alteração.");
        }
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
