package inss.gca.mogi.mogi.controller.update;

import inss.gca.mogi.mogi.dao.LogServidorDAO;
import inss.gca.mogi.mogi.dto.BuscaDTO;
import inss.gca.mogi.mogi.model.Arquivo;
import inss.gca.mogi.mogi.model.LogServidor;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.ArquivoService;
import inss.gca.mogi.mogi.service.SeguradoService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.time.LocalDateTime;
import java.util.TreeMap;

public class AlterarArquivoController {

    @FXML private TextField numCaixa;
    @FXML private TextField cpf;
    @FXML private TextField nb;
    @FXML private ChoiceBox<String> tipoSelect;
    @FXML private TextField viewTipoBeneficioField;
    @FXML private TextField nomeSegurado;
    @FXML private TextField aps;
    @FXML private Button cadastrarButton;

    private Arquivo arquivoOriginal;
    private int idServidor;

    private final ArquivoService arquivoService = new ArquivoService();
    private final SeguradoService seguradoService = new SeguradoService();
    private final LogServidorDAO logDAO = new LogServidorDAO();
    private TreeMap<String, String> tipoBeneficioMap;

    @FXML
    public void initialize() {
        inicializarTipoBeneficioMap();

        tipoSelect.setItems(FXCollections.observableArrayList(tipoBeneficioMap.keySet()));
        tipoSelect.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateTipoBeneficioField(newVal);
        });

        applyCpfMask(cpf);
    }

    public void initData(Arquivo arquivo, int idServidor, String perfilServidor) {
        this.arquivoOriginal = arquivo;
        this.idServidor = idServidor;

        tipoSelect.getSelectionModel().select(arquivo.getTipoBeneficio());

        preencherCampos(arquivo);
        aplicarPermissoes();
    }

    private void preencherCampos(Arquivo arquivo) {
        nb.setText(arquivo.getNb());
        tipoSelect.setValue(arquivo.getTipoBeneficio());
        numCaixa.setText(arquivo.getCodCaixa());
        cpf.setText(buscarCpfPorIdSegurado(arquivo.getIdSegurado()));
        nomeSegurado.setText(buscarNomePorIdSegurado(arquivo.getIdSegurado()));
        aps.setText("Mogi Das Cruzes");
        updateTipoBeneficioField(arquivo.getTipoBeneficio());
    }

    private void aplicarPermissoes() {
        try {
            PermissaoValidator.validarPodeAlterarCpfNb(idServidor);
        } catch (Exception e) {
            cpf.setDisable(true);
            nb.setDisable(true);
        }

        try {
            PermissaoValidator.validarPodeAlterarNomeSegurado(idServidor);
        } catch (Exception e) {
            nomeSegurado.setDisable(true);
        }

        try {
            PermissaoValidator.validarPodeAlterarCaixa(idServidor);
        } catch (Exception e) {
            numCaixa.setDisable(true);
        }

        cadastrarButton.setText("SALVAR ALTERAÇÕES");
    }

    @FXML
    private void cadastrar(ActionEvent event) {
        try {
            boolean alterouAlgo = false;

            if (!nb.isDisabled() && !nb.getText().equals(arquivoOriginal.getNb())) {
                arquivoService.atualizarNb(arquivoOriginal.getId(), nb.getText());
                registrarLog("Alteração de NB", "De: " + arquivoOriginal.getNb() + " Para: " + nb.getText());
                alterouAlgo = true;
            }

            if (!cpf.isDisabled() && !cpf.getText().equals(buscarCpfPorIdSegurado(arquivoOriginal.getIdSegurado()))) {
                seguradoService.atualizarCPF(arquivoOriginal.getIdSegurado(), cpf.getText());
                registrarLog("Alteração de CPF", "De: " + buscarCpfPorIdSegurado(arquivoOriginal.getIdSegurado()) + " Para: " + cpf.getText());
                alterouAlgo = true;
            }

            if (!nomeSegurado.isDisabled() && !nomeSegurado.getText().equals(buscarNomePorIdSegurado(arquivoOriginal.getIdSegurado()))) {
                seguradoService.atualizarNome(arquivoOriginal.getIdSegurado(), nomeSegurado.getText());
                registrarLog("Alteração de nome", "De: " + buscarNomePorIdSegurado(arquivoOriginal.getIdSegurado()) + " Para: " + nomeSegurado.getText());
                alterouAlgo = true;
            }

            if (!numCaixa.isDisabled() && !numCaixa.getText().equals(arquivoOriginal.getCodCaixa())) {
                arquivoService.atualizarCaixa(arquivoOriginal.getId(), numCaixa.getText());
                registrarLog("Alteração de código da caixa", "De: " + arquivoOriginal.getCodCaixa() + " Para: " + numCaixa.getText());
                alterouAlgo = true;
            }

            if (!tipoSelect.isDisabled() && !tipoSelect.getValue().equals(arquivoOriginal.getTipoBeneficio())) {
                arquivoOriginal.setTipoBeneficio(tipoSelect.getValue());
                arquivoService.atualizar(arquivoOriginal);
                registrarLog("Alteração de tipo benefício", "Para: " + tipoSelect.getValue());
                alterouAlgo = true;
            }

            if (alterouAlgo) {
                exibirAlerta("Sucesso", "Alterações salvas com sucesso.");
                cadastrarButton.getScene().getWindow().hide();
            } else {
                exibirAlerta("Aviso", "Nenhuma alteração detectada.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            exibirAlerta("Erro", "Erro ao salvar alterações: " + e.getMessage());
        }
    }

    public void initDataBuscaDTO(BuscaDTO dto, int idServidor, String perfilServidor) {
        this.idServidor = idServidor;

        tipoSelect.setItems(FXCollections.observableArrayList(tipoBeneficioMap.keySet()));
        tipoSelect.getSelectionModel().select(dto.getTipoBeneficio());

        nb.setText(dto.getNb());
        cpf.setText(dto.getCpfSegurado());
        nomeSegurado.setText(dto.getNomeSegurado());
        numCaixa.setText(dto.getCodCaixa());
        aps.setText(dto.getRua() + " - Andar " + dto.getAndar() + ", Prateleira " + dto.getPrateleira());
        updateTipoBeneficioField(dto.getTipoBeneficio());

        this.arquivoOriginal = new Arquivo();
        arquivoOriginal.setId(dto.getId());
        arquivoOriginal.setNb(dto.getNb());
        arquivoOriginal.setTipoBeneficio(dto.getTipoBeneficio());
        arquivoOriginal.setCodCaixa(dto.getCodCaixa());
        arquivoOriginal.setIdServidor(idServidor);

        try {
            int idSegurado = seguradoService.buscarPorCpfUnico(dto.getCpfSegurado()).getId();
            arquivoOriginal.setIdSegurado(idSegurado);
        } catch (Exception e) {
            exibirAlerta("Aviso", "Não foi possível localizar o segurado pelo CPF.");
            arquivoOriginal.setIdSegurado(-1);
        }

        aplicarPermissoes();
    }

    private void registrarLog(String acao, String detalhes) {
        LogServidor log = new LogServidor();
        log.setServidorId(idServidor);
        log.setAcao(acao);
        log.setDataHora(LocalDateTime.now());
        log.setDetalhes("Arquivo ID: " + arquivoOriginal.getId() + " | " + detalhes);
        logDAO.registrarLog(log);
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private String buscarCpfPorIdSegurado(int idSegurado) {
        try {
            return seguradoService.buscarPorId(idSegurado).getCpf();
        } catch (Exception e) {
            return "";
        }
    }

    private String buscarNomePorIdSegurado(int idSegurado) {
        try {
            return seguradoService.buscarPorId(idSegurado).getNomeSegurado();
        } catch (Exception e) {
            return "";
        }
    }

    private void applyCpfMask(TextField textField) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            private boolean changing = false;
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (changing) return;
                changing = true;

                String digits = newValue.replaceAll("[^\\d]", "");
                if (digits.length() > 11) digits = digits.substring(0, 11);

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    formatted.append(digits.charAt(i));
                    if (i == 2 || i == 5) formatted.append('.');
                    else if (i == 8) formatted.append('-');
                }

                int caret = textField.getCaretPosition();
                textField.setText(formatted.toString());
                textField.positionCaret(Math.min(caret, textField.getText().length()));
                changing = false;
            }
        });
    }

    private void updateTipoBeneficioField(String tipo) {
        if (tipo == null) {
            viewTipoBeneficioField.setText("Tipo inválido");
            return;
        }
        String descricao = tipoBeneficioMap.getOrDefault(tipo, "Tipo de benefício desconhecido");
        viewTipoBeneficioField.setText(descricao);
    }

    private void inicializarTipoBeneficioMap() {
        tipoBeneficioMap = new TreeMap<>();
        tipoBeneficioMap.put("01", "Pensão por morte do trabalhador rural");
        tipoBeneficioMap.put("02", "Pensão por morte por acidente do trabalho do trabalhador rural");
        tipoBeneficioMap.put("03", "Pensão por morte do empregador rural");
        tipoBeneficioMap.put("04", "Aposentadoria por invalidez do trabalhador rural");
        tipoBeneficioMap.put("05", "Aposentadoria por invalidez por acidente do trabalho do trabalhador rural");
        tipoBeneficioMap.put("06", "Aposentadoria por invalidez do empregador rural");
        tipoBeneficioMap.put("07", "Aposentadoria por idade do trabalhador rural");
        tipoBeneficioMap.put("08", "Aposentadoria por idade do empregador rural");
        tipoBeneficioMap.put("10", "Auxílio-doença por acidente do trabalho do trabalhador rural");
        tipoBeneficioMap.put("11", "Renda mensal vitalícia por invalidez do trabalhador rural (Lei nº 6.179/74)");
        tipoBeneficioMap.put("12", "Renda mensal vitalícia por idade do trabalhador rural (Lei nº 6.179/74)");
        tipoBeneficioMap.put("13", "Auxílio-doença do trabalhador rural");
        tipoBeneficioMap.put("15", "Auxílio-reclusão do trabalhador rural");
        tipoBeneficioMap.put("21", "Pensão por morte previdenciária");
        tipoBeneficioMap.put("22", "Pensão por morte estatutária");
        tipoBeneficioMap.put("23", "Aposentadoria por invalidez previdenciária");
        tipoBeneficioMap.put("24", "Aposentadoria por idade previdenciária");
        tipoBeneficioMap.put("25", "Auxílio-doença previdenciário");
        tipoBeneficioMap.put("26", "Auxílio-reclusão previdenciário");
        tipoBeneficioMap.put("27", "Renda mensal vitalícia por invalidez previdenciária");
        tipoBeneficioMap.put("28", "Renda mensal vitalícia por idade previdenciária");
        tipoBeneficioMap.put("29", "Pensão por morte de servidor público");
        tipoBeneficioMap.put("30", "Aposentadoria por invalidez de servidor público");
        tipoBeneficioMap.put("31", "Aposentadoria por idade de servidor público");
        tipoBeneficioMap.put("32", "Auxílio-doença de servidor público");
        tipoBeneficioMap.put("33", "Auxílio-reclusão de servidor público");
        tipoBeneficioMap.put("34", "Renda mensal vitalícia por invalidez de servidor público");
        tipoBeneficioMap.put("35", "Renda mensal vitalícia por idade de servidor público");
        tipoBeneficioMap.put("40", "Pensão por morte de dependente de servidor público");
        tipoBeneficioMap.put("41", "Aposentadoria por invalidez de dependente de servidor público");
        tipoBeneficioMap.put("42", "Aposentadoria por idade de dependente de servidor público");
        tipoBeneficioMap.put("43", "Auxílio-doença de dependente de servidor público");
        tipoBeneficioMap.put("44", "Auxílio-reclusão de dependente de servidor público");
        tipoBeneficioMap.put("45", "Renda mensal vitalícia por invalidez de dependente de servidor público");
        tipoBeneficioMap.put("46", "Renda mensal vitalícia por idade de dependente de servidor público");
        tipoBeneficioMap.put("50", "Pensão por morte de dependente do trabalhador");
        tipoBeneficioMap.put("51", "Aposentadoria por invalidez de dependente do trabalhador");
        tipoBeneficioMap.put("52", "Aposentadoria por idade de dependente do trabalhador");
        tipoBeneficioMap.put("53", "Auxílio-doença de dependente do trabalhador");
        tipoBeneficioMap.put("54", "Auxílio-reclusão de dependente do trabalhador");
        tipoBeneficioMap.put("55", "Renda mensal vitalícia por invalidez de dependente do trabalhador");
        tipoBeneficioMap.put("56", "Renda mensal vitalícia por idade de dependente do trabalhador");
        tipoBeneficioMap.put("57", "Pensão por morte de dependente do trabalhador rural");
        tipoBeneficioMap.put("58", "Aposentadoria por invalidez de dependente do trabalhador rural");
        tipoBeneficioMap.put("59", "Aposentadoria por idade de dependente do trabalhador rural");
        tipoBeneficioMap.put("60", "Auxílio-doença de dependente do trabalhador rural");
        tipoBeneficioMap.put("61", "Auxílio-reclusão de dependente do trabalhador rural");
        tipoBeneficioMap.put("62", "Renda mensal vitalícia por invalidez de dependente do trabalhador rural");
        tipoBeneficioMap.put("63", "Renda mensal vitalícia por idade de dependente do trabalhador rural");
        tipoBeneficioMap.put("64", "Pensão por morte de dependente de servidor público rural");
        tipoBeneficioMap.put("65", "Aposentadoria por invalidez de dependente de servidor público rural");
        tipoBeneficioMap.put("66", "Aposentadoria por idade de dependente de servidor público rural");
        tipoBeneficioMap.put("67", "Auxílio-doença de dependente de servidor público rural");
        tipoBeneficioMap.put("68", "Auxílio-reclusão de dependente de servidor público rural");
        tipoBeneficioMap.put("69", "Renda mensal vitalícia por invalidez de dependente de servidor público rural");
        tipoBeneficioMap.put("70", "Renda mensal vitalícia por idade de dependente de servidor público rural");
        tipoBeneficioMap.put("71", "Pensão por morte do trabalhador autônomo");
        tipoBeneficioMap.put("72", "Aposentadoria por invalidez do trabalhador autônomo");
        tipoBeneficioMap.put("73", "Aposentadoria por idade do trabalhador autônomo");
        tipoBeneficioMap.put("74", "Auxílio-doença do trabalhador autônomo");
        tipoBeneficioMap.put("75", "Auxílio-reclusão do trabalhador autônomo");
        tipoBeneficioMap.put("76", "Renda mensal vitalícia por invalidez do trabalhador autônomo");
        tipoBeneficioMap.put("77", "Renda mensal vitalícia por idade do trabalhador autônomo");
        tipoBeneficioMap.put("78", "Pensão por morte do dependente do trabalhador autônomo");
        tipoBeneficioMap.put("79", "Aposentadoria por invalidez do dependente do trabalhador autônomo");
        tipoBeneficioMap.put("80", "Aposentadoria por idade do dependente do trabalhador autônomo");
        tipoBeneficioMap.put("81", "Auxílio-doença do dependente do trabalhador autônomo");
        tipoBeneficioMap.put("82", "Auxílio-reclusão do dependente do trabalhador autônomo");
        tipoBeneficioMap.put("83", "Renda mensal vitalícia por invalidez do dependente do trabalhador autônomo");
        tipoBeneficioMap.put("84", "Renda mensal vitalícia por idade do dependente do trabalhador autônomo");
        tipoBeneficioMap.put("85", "Pensão por morte do trabalhador empregado");
        tipoBeneficioMap.put("86", "Aposentadoria por invalidez do trabalhador empregado");
        tipoBeneficioMap.put("87", "Aposentadoria por idade do trabalhador empregado");
        tipoBeneficioMap.put("88", "Auxílio-doença do trabalhador empregado");
        tipoBeneficioMap.put("89", "Auxílio-reclusão do trabalhador empregado");
        tipoBeneficioMap.put("90", "Renda mensal vitalícia por invalidez do trabalhador empregado");
        tipoBeneficioMap.put("91", "Renda mensal vitalícia por idade do trabalhador empregado");
        tipoBeneficioMap.put("92", "Aposentadoria por invalidez por acidente do trabalho");
        tipoBeneficioMap.put("93", "Pensão por morte por acidente do trabalho");
        tipoBeneficioMap.put("94", "Auxílio-acidente por acidente do trabalho");
        tipoBeneficioMap.put("95", "Auxílio-suplementar por acidente do trabalho");
        tipoBeneficioMap.put("96", "Pensão especial às pessoas atingidas pela hanseníase (Lei nº 11.520/2007)");
    }
}
