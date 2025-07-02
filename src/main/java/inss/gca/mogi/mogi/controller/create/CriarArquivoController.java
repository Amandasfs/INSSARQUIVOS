package inss.gca.mogi.mogi.controller.create;

import inss.gca.mogi.mogi.model.Arquivo;
import inss.gca.mogi.mogi.model.Segurado;
import inss.gca.mogi.mogi.service.ArquivoService;
import inss.gca.mogi.mogi.service.SeguradoService;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller responsável pela tela de cadastro de arquivos.
 * Faz a comunicação com a camada de serviço e lida com a interface gráfica.
 */
public class CriarArquivoController {

    @FXML
    private TextField nomeSegurado;

    @FXML
    private TextField cpf;

    @FXML
    private ChoiceBox<String> tipoSelect;

    @FXML
    private TextField viewTipoBeneficioField;

    @FXML
    private TextField nb;

    @FXML
    private TextField numCaixa;

    @FXML
    private TextField aps;

    @FXML
    private Button cadastrarButton;

    private final ArquivoService arquivoService = new ArquivoService();
    private final SeguradoService seguradoService = new SeguradoService();

    // ID do servidor logado (use aqui o ID e não a matrícula)
    private final int idServidorLogado = 1;

    /**
     * Método chamado antes da abertura da tela para preencher os campos NB e Caixa.
     *
     * @param codCaixa código da caixa.
     * @param nbValor número de benefício.
     */
    public void setDadosDaCaixaENb(String codCaixa, String nbValor) {
        numCaixa.setText(codCaixa);
        nb.setText(nbValor);

        // Tornar os campos não editáveis para garantir que usuário não modifique
        numCaixa.setEditable(false);
        nb.setEditable(false);
    }

    /**
     * Inicializa o ChoiceBox de tipo de benefício e configura listener para exibir no campo ao lado.
     */
    @FXML
    public void initialize() {
        tipoSelect.getItems().addAll("APOSENTADORIA", "PENSÃO", "AUXÍLIO", "OUTRO");

        tipoSelect.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            viewTipoBeneficioField.setText(newVal);
        });
    }

    /**
     * Ação ao clicar no botão de cadastro.
     *
     * @param event evento de clique.
     */
    @FXML
    public void cadastrar(ActionEvent event) {
        String nome = nomeSegurado.getText().trim();
        String cpfSegurado = cpf.getText().trim();
        String tipoBeneficio = tipoSelect.getValue();
        String nbDigitado = nb.getText().trim();
        String codCaixa = numCaixa.getText().trim();

        if (nome.isEmpty() || cpfSegurado.isEmpty() || tipoBeneficio == null || nbDigitado.isEmpty() || codCaixa.isEmpty()) {
            showAlert("Campos obrigatórios", "Preencha todos os campos antes de cadastrar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Verificar se o segurado já existe pelo CPF
            Segurado seguradoExistente;
            try {
                seguradoExistente = seguradoService.buscarPorCpfUnico(cpfSegurado);
            } catch (ObjectNotFoundException e) {
                // Se não existir, criar novo segurado
                Segurado novoSegurado = new Segurado();
                novoSegurado.setNomeSegurado(nome);
                novoSegurado.setCpf(cpfSegurado);
                novoSegurado.setIdServidor(idServidorLogado);
                seguradoExistente = seguradoService.criarSegurado(novoSegurado);
            }

            // Criar o objeto Arquivo
            Arquivo arquivo = new Arquivo();
            arquivo.setNb(nbDigitado);
            arquivo.setTipoBeneficio(tipoBeneficio);
            arquivo.setIdSegurado(seguradoExistente.getId());
            arquivo.setCodCaixa(codCaixa);
            arquivo.setIdServidor(idServidorLogado);

            // Persistir o arquivo
            arquivoService.criar(arquivo);

            showAlert("Sucesso", "Arquivo cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            limparCampos();

        } catch (DataIntegrityViolationException e) {
            showAlert("Erro ao salvar", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erro inesperado", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Limpa os campos da tela após o cadastro.
     * Obs: Mantemos os campos NB e Caixa preenchidos.
     */
    private void limparCampos() {
        nomeSegurado.clear();
        cpf.clear();
        tipoSelect.getSelectionModel().clearSelection();
        viewTipoBeneficioField.clear();
        aps.clear();
    }

    /**
     * Exibe um alerta para o usuário.
     *
     * @param titulo   título da janela.
     * @param mensagem mensagem do alerta.
     * @param tipo     tipo de alerta.
     */
    private void showAlert(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
