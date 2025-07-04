package inss.gca.mogi.mogi.controller.profile;

import inss.gca.mogi.mogi.dto.BuscaDTO;
import inss.gca.mogi.mogi.dto.CaixaDTO;
import inss.gca.mogi.mogi.model.Caixa;
import inss.gca.mogi.mogi.service.ArquivoService;
import inss.gca.mogi.mogi.service.CaixaService;
import inss.gca.mogi.mogi.service.SeguradoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GerenteController {

    @FXML private ImageView closeImageView;
    @FXML private TextField buscaTextField;
    @FXML private ChoiceBox<String> filtroChoiceBox;
    @FXML private TableView<BuscaDTO> tableResultado;
    @FXML
    private Button buscarButton, buscarCaixasButton;
    @FXML private TableColumn<BuscaDTO, String> colTipoBeneficio;
    @FXML private TableColumn<BuscaDTO, String> colNb;
    @FXML private TableColumn<BuscaDTO, String> colNomeSegurado;
    @FXML private TableColumn<BuscaDTO, String> colCodCaixa;
    @FXML private TableColumn<BuscaDTO, String> colRua;
    @FXML private TableColumn<BuscaDTO, Integer> colPrateleira;
    @FXML private TableColumn<BuscaDTO, String> colAndar;

    private final ArquivoService arquivoService = new ArquivoService();
    private final CaixaService caixaService = new CaixaService();
    private final SeguradoService seguradoService = new SeguradoService();

    @FXML
    public void initialize() {
        filtroChoiceBox.setItems(FXCollections.observableArrayList("CPF", "NB", "Caixa"));
        filtroChoiceBox.getSelectionModel().selectFirst();

        colCodCaixa.setCellValueFactory(new PropertyValueFactory<>("codCaixa"));
        colNb.setCellValueFactory(new PropertyValueFactory<>("nb"));
        colTipoBeneficio.setCellValueFactory(new PropertyValueFactory<>("tipoBeneficio"));
        colNomeSegurado.setCellValueFactory(new PropertyValueFactory<>("nomeSegurado"));
        colRua.setCellValueFactory(new PropertyValueFactory<>("rua"));
        colPrateleira.setCellValueFactory(new PropertyValueFactory<>("prateleira"));
        colAndar.setCellValueFactory(new PropertyValueFactory<>("andar"));
    }

    @FXML
    private void onBuscar() {
        String filtro = filtroChoiceBox.getValue();
        String busca = buscaTextField.getText().trim();

        if (busca.isEmpty()) {
            showAlert("Aviso", "Informe um valor para busca.");
            return;
        }

        try {
            switch (filtro) {
                case "CPF" -> buscarPorCpf(busca);
                case "NB" -> buscarPorNb(busca);
                case "Caixa" -> buscarPorCaixa(busca);
                default -> showAlert("Erro", "Filtro desconhecido.");
            }
        } catch (Exception e) {
            showAlert("Erro na busca", e.getMessage());
            clearTabela();
        }
    }

    private void buscarPorCpf(String cpf) {
        List<BuscaDTO> resultados = seguradoService.buscarPorCpf(cpf.replaceAll("\\D", ""));
        if (resultados.isEmpty()) {
            showAlert("Nenhum resultado", "Nenhum arquivo encontrado para o CPF informado.");
            clearTabela();
        } else {
            atualizarTabela(resultados);
        }
    }

    private void buscarPorNb(String nb) {
        BuscaDTO dto;
        try {
            dto = arquivoService.buscarPorNbComVerificacao(nb);
        } catch (RuntimeException e) {
            showAlert("Informação", e.getMessage());
            clearTabela();
            return;
        }

        if (dto.getId() == 0) {
            List<BuscaDTO> lista = new ArrayList<>();
            lista.add(dto);
            atualizarTabela(lista);
            showAlert("Aviso", "Seu arquivo não está cadastrado, mas o NB pertence à caixa: " + dto.getCodCaixa());
        } else {
            atualizarTabela(List.of(dto));
        }
    }

    private void buscarPorCaixa(String codCaixa) {
        CaixaDTO caixaDto;
        try {
            caixaDto = caixaService.buscarPorCodigoComArquivos(codCaixa);
        } catch (RuntimeException e) {
            showAlert("Nenhum resultado", e.getMessage());
            clearTabela();
            return;
        }

        List<BuscaDTO> resultados = new ArrayList<>();

        if (caixaDto.getNbs() == null || caixaDto.getNbs().isEmpty()) {
            BuscaDTO caixaSomente = new BuscaDTO();
            caixaSomente.setCodCaixa(caixaDto.getCodCaixa());
            caixaSomente.setRua(caixaDto.getRua());
            caixaSomente.setPrateleira(caixaDto.getPrateleira());
            caixaSomente.setAndar(caixaDto.getAndar());
            caixaSomente.setNb(caixaDto.getNbInicial() + " até " + caixaDto.getNbFinal());
            caixaSomente.setTipoBeneficio("Nenhum arquivo cadastrado");
            resultados.add(caixaSomente);
        } else {
            for (String nb : caixaDto.getNbs()) {
                BuscaDTO dto = new BuscaDTO();
                dto.setCodCaixa(caixaDto.getCodCaixa());
                dto.setNb(nb);
                dto.setRua(caixaDto.getRua());
                dto.setPrateleira(caixaDto.getPrateleira());
                dto.setAndar(caixaDto.getAndar());
                dto.setTipoBeneficio("Arquivo cadastrado");
                resultados.add(dto);
            }
        }

        atualizarTabela(resultados);
    }

    @FXML
    private void onBuscarTodas() {
        try {
            List<Caixa> caixas = caixaService.buscarTodasCaixas();
            if (caixas.isEmpty()) {
                showAlert("Nenhum resultado", "Erro ao buscar caixas");
                clearTabela();
                return;
            }

            List<BuscaDTO> resultados = new ArrayList<>();

            for (Caixa caixa : caixas) {
                BuscaDTO dto = new BuscaDTO();
                dto.setCodCaixa(caixa.getCodCaixa());
                dto.setRua(caixa.getRua());
                dto.setPrateleira(caixa.getPrateleira());
                dto.setAndar(caixa.getAndar());
                dto.setNb(caixa.getNbInicial() + " até " + caixa.getNbFinal());
                dto.setTipoBeneficio("Caixa cadastrada");
                // Campos como nomeSegurado, cpfSegurado e outros podem ficar vazios ou ajustar conforme necessidade
                resultados.add(dto);
            }

            atualizarTabela(resultados);

        } catch (Exception e) {
            showAlert("Nenhum resultado", "Erro ao buscar caixas");
            clearTabela();
        }
    }

    private void atualizarTabela(List<BuscaDTO> dados) {
        ObservableList<BuscaDTO> lista = FXCollections.observableArrayList(dados);
        tableResultado.setItems(lista);
    }

    private void clearTabela() {
        tableResultado.getItems().clear();
    }

    private void showAlert(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    public void handleClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) closeImageView.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleUserIconClick(MouseEvent mouseEvent) {
        abrirTela("/update/AlterarSenha.fxml", "TROCAR SENHA");
    }

    @FXML
    public void handleCadCXIconClick(MouseEvent mouseEvent) {
        abrirTela("/create/CriarCaixa.fxml", "CADASTRAR CAIXA");
    }

    @FXML
    public void handleALTCXIconClick(MouseEvent mouseEvent) {
        abrirTela("/select/SelectAltCaixa.fxml", "ALTERAR CAIXA");
    }

    @FXML
    public void handleCADARIconClick(MouseEvent mouseEvent) {
        abrirTela("/select/SelectCadArquivo.fxml", "CADASTRAR ARQUIVO");
    }

    @FXML
    public void handleALTARIconClick(MouseEvent mouseEvent) {
        abrirTela("/select/SelectAltArquivo.fxml", "ALTERAR ARQUIVO");
    }

    @FXML
    public void handlePrint(MouseEvent event) {
        abrirTela("/select/SelecionarCaixaEtiqueta.fxml", "Imprimir Etiqueta");
    }

    @FXML
    public void handleDownload(MouseEvent mouseEvent) {
        abrirTela("/util/Baixar.fxml", "DOWNLOAD");
    }

    public void handleUserCreateIconClick(MouseEvent mouseEvent) {
        abrirTela("/create/CriarServidor.fxml", "CRIAR SERVIDOR");
    }
    public void handleUserPainelClick(MouseEvent mouseEvent) {
        abrirTela("/profile/PainelServidor.fxml", "PAINEL USUARIO");
    }

    public void handleEXCIconClick(MouseEvent mouseEvent){
        abrirTela("/select/SelectExcluirSelect.fxml", "PAINEL EXCLUIR");
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
}
