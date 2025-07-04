package inss.gca.mogi.mogi.controller.select;

import inss.gca.mogi.mogi.dto.CaixaDTO;
import inss.gca.mogi.mogi.service.CaixaService;
import inss.gca.mogi.mogi.util.EtiquetaPrinter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SelectEtiqController {

    @FXML
    private TextField txtCodCaixa;

    private final CaixaService caixaService = new CaixaService();

    @FXML
    public void onGerarEtiqueta(ActionEvent event) {
        String codigo = txtCodCaixa.getText().trim().toUpperCase();
        try {
            CaixaDTO dto = caixaService.buscarPorCodigoComArquivos(codigo);
            if (dto == null) {
                showAlert("Caixa não encontrada", "Nenhuma caixa com esse código.");
                return;
            }
            abrirTelaEtiqueta(dto);
        } catch (Exception e) {
            showAlert("Erro", "Erro ao buscar caixa: " + e.getMessage());
        }
    }

    private void abrirTelaEtiqueta(CaixaDTO dto) {
        EtiquetaPrinter.gerarEtiqueta(dto); // classe util para gerar a etiqueta
        Stage stage = (Stage) txtCodCaixa.getScene().getWindow();
        stage.close(); // fecha janela de seleção
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
