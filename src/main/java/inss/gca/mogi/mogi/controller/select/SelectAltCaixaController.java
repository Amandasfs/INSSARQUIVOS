package inss.gca.mogi.mogi.controller.select;

import inss.gca.mogi.mogi.model.Caixa;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.CaixaService;
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

public class SelectAltCaixaController {

    @FXML
    private TextField numCaixa;

    private final CaixaService caixaService = new CaixaService();

    // Simulando dados do servidor logado (poderia vir de um contexto autenticado)
    private final int idServidorLogado = 1; // exemplo
    private final String perfilServidor = "ARQUIVISTA_AUTORIZADO"; // exemplo

    @FXML
    public void verificarArquivo(ActionEvent event) {
        String codigo = numCaixa.getText();

        if (codigo.isEmpty()) {
            exibirAlerta("Código obrigatório", "Digite o código da caixa.");
            return;
        }

        try {
            // Busca a caixa pelo código
            Caixa caixa = caixaService.buscaPorCodigoCaixa(codigo);

            // Verifica se o servidor tem permissão para alterar ao menos um dos campos
            boolean podeAlterarAlgo = verificarPermissoes();

            if (!podeAlterarAlgo) {
                exibirAlerta("Acesso negado", "Você não tem permissão para alterar essa caixa.");
                return;
            }

            // Abre a tela de alteração
            abrirTelaAlteracao(caixa);

        } catch (ObjectNotFoundException e) {
            exibirAlerta("Caixa não encontrada", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro inesperado ao buscar caixa.");
        }
    }

    private boolean verificarPermissoes() {
        try {
            return
                    servidorPode(() -> PermissaoValidator.validarPodeAlterarLocalCaixa(idServidorLogado)) ||
                            servidorPode(() -> PermissaoValidator.validarPodeAlterarCpfNb(idServidorLogado)) ||
                            servidorPode(() -> PermissaoValidator.validarPodeAlterarCodigoCaixa(idServidorLogado));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean servidorPode(Runnable permissao) {
        try {
            permissao.run();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void abrirTelaAlteracao(Caixa caixa) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/update/AlterarCaixa.fxml"));
        Parent root = loader.load();

        // Passa dados para o controller de alteração
        inss.gca.mogi.mogi.controller.update.AlterarCaixaController controller = loader.getController();
        controller.initData(caixa, idServidorLogado, perfilServidor);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Alterar Caixa");
        stage.show();

        // Fecha a tela atual (opcional)
        numCaixa.getScene().getWindow().hide();
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
