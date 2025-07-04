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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class ConsultorController {

    @FXML
    private ChoiceBox<String> filtroChoiceBox;

    @FXML
    private TextField buscaTextField;

    @FXML
    private Button buscarButton, buscarCaixasButton;


    @FXML
    private TableView<BuscaDTO> tableResultado;

    @FXML
    private TableColumn<BuscaDTO, String> colTipoBeneficio;
    @FXML
    private TableColumn<BuscaDTO, String> colNb;
    @FXML
    private TableColumn<BuscaDTO, String> colNomeSegurado;
    @FXML
    private TableColumn<BuscaDTO, String> colCodCaixa;
    @FXML
    private TableColumn<BuscaDTO, String> colRua;
    @FXML
    private TableColumn<BuscaDTO, String> colCpf;
    @FXML
    private TableColumn<BuscaDTO, Integer> colPrateleira;
    @FXML
    private TableColumn<BuscaDTO, String> colAndar;

    @FXML
    private ImageView closeImageView;

    private final SeguradoService seguradoService = new SeguradoService();
    private final ArquivoService arquivoService = new ArquivoService();
    private final CaixaService caixaService = new CaixaService();

    @FXML
    public void initialize() {
        // Configura ChoiceBox
        filtroChoiceBox.setItems(FXCollections.observableArrayList("CPF", "NB", "Caixa"));
        filtroChoiceBox.getSelectionModel().selectFirst();

        // Configura colunas da tabela
        colCodCaixa.setCellValueFactory(new PropertyValueFactory<>("codCaixa"));
        colNb.setCellValueFactory(new PropertyValueFactory<>("nb"));
        colTipoBeneficio.setCellValueFactory(new PropertyValueFactory<>("tipoBeneficio"));
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpfSegurado"));
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
            showAlert(Alert.AlertType.WARNING, "Aviso", "Informe um valor para busca.");
            return;
        }

        try {
            switch (filtro) {
                case "CPF":
                    buscarPorCpf(busca);
                    break;
                case "NB":
                    buscarPorNb(busca);
                    break;
                case "Caixa":
                    buscarPorCaixa(busca);
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Erro", "Filtro desconhecido.");
                    break;
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro na busca", e.getMessage());
            clearTabela();
        }
    }

    @FXML
    private void onBuscarTodas() {
        try {
            List<Caixa> caixas = caixaService.buscarTodasCaixas();
            if (caixas.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Nenhum resultado", "Nenhuma caixa cadastrada.");
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
            showAlert(Alert.AlertType.ERROR, "Erro ao buscar caixas", e.getMessage());
            clearTabela();
        }
    }


    private void buscarPorCpf(String cpf) {
        List<BuscaDTO> resultados = seguradoService.buscarPorCpf(cpf.replaceAll("\\D", ""));
        if (resultados.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Nenhum resultado", "Nenhum arquivo encontrado para o CPF informado.");
            clearTabela();
        } else {
            atualizarTabela(resultados);
        }
    }

    private void buscarPorNb(String nb) {
        BuscaDTO dto = null;
        try {
            dto = arquivoService.buscarPorNbComVerificacao(nb);
        } catch (RuntimeException e) {
            // erro customizado, pode ser "arquivo não cadastrado nem pertence a caixa"
            showAlert(Alert.AlertType.INFORMATION, "Informação", e.getMessage());
            clearTabela();
            return;
        }

        // Se id == 0, arquivo não cadastrado, mas caixa encontrada
        if (dto.getId() == 0) {
            // Criar uma lista com essa única entrada para mostrar na tabela
            List<BuscaDTO> lista = new ArrayList<>();
            lista.add(dto);
            atualizarTabela(lista);

            showAlert(Alert.AlertType.INFORMATION, "Aviso",
                    "Seu arquivo não está cadastrado, mas o NB pertence à caixa: " + dto.getCodCaixa());
        } else {
            // Arquivo cadastrado normal
            atualizarTabela(List.of(dto));
        }
    }

    private void buscarPorCaixa(String codCaixa) {
        CaixaDTO caixaDto;
        try {
            caixaDto = caixaService.buscarPorCodigoComArquivos(codCaixa);
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.INFORMATION, "Nenhum resultado", e.getMessage());
            clearTabela();
            return;
        }

        List<BuscaDTO> resultados = new ArrayList<>();

        if (caixaDto.getNbs() == null || caixaDto.getNbs().isEmpty()) {
            // Nenhum arquivo cadastrado, então monta BuscaDTO com intervalo da caixa só para mostrar
            BuscaDTO caixaSomente = new BuscaDTO();
            caixaSomente.setCodCaixa(caixaDto.getCodCaixa());
            caixaSomente.setRua(caixaDto.getRua());
            caixaSomente.setPrateleira(caixaDto.getPrateleira());
            caixaSomente.setAndar(caixaDto.getAndar());
            caixaSomente.setNb(caixaDto.getNbInicial() + " até " + caixaDto.getNbFinal());
            caixaSomente.setTipoBeneficio("Nenhum arquivo cadastrado");
            resultados.add(caixaSomente);
        } else {
            // Para cada nb cadastrado, cria BuscaDTO para mostrar na tabela (sem detalhes do segurado para simplicidade)
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

    private void atualizarTabela(List<BuscaDTO> dados) {
        ObservableList<BuscaDTO> lista = FXCollections.observableArrayList(dados);
        tableResultado.setItems(lista);
    }

    private void clearTabela() {
        tableResultado.getItems().clear();
    }

    private void showAlert(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void handleClose(MouseEvent event) {
        // Fecha janela atual
        ((javafx.stage.Stage) closeImageView.getScene().getWindow()).close();
    }
}
