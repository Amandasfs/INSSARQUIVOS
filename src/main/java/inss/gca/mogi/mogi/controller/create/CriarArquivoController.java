package inss.gca.mogi.mogi.controller.create;

import inss.gca.mogi.mogi.model.Arquivo;
import inss.gca.mogi.mogi.model.Segurado;
import inss.gca.mogi.mogi.service.ArquivoService;
import inss.gca.mogi.mogi.service.SeguradoService;
import inss.gca.mogi.mogi.service.exceptions.DataIntegrityViolationException;
import inss.gca.mogi.mogi.service.exceptions.ObjectNotFoundException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Map;
import java.util.TreeMap;

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

    private Map<String, String> tipoBeneficioMap;

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
        // Inicializa o mapa de tipos de benefícios
        inicializarTipoBeneficioMap();

        // Preenche o ChoiceBox com as opções (exibindo as chaves, ex: "01", "02", etc)
        tipoSelect.getItems().addAll(tipoBeneficioMap.keySet());

        // Configura o listener para atualizar o campo de visualização do tipo
        tipoSelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateTipoBeneficioField(newValue);
        });

        // Aplica a máscara no campo CPF (usando o método igual ao do AlterarArquivoController)
        applyCpfMask(cpf);

        // Configura a ação do botão cadastrar para chamar o método cadastrar
        cadastrarButton.setOnAction(event -> cadastrar(event));
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

        // Remove formatação do CPF para validar
        String cpfLimpo = cpfSegurado.replaceAll("[^\\d]", "");

        // Validação de campos obrigatórios
        if (nome.isEmpty() || cpfLimpo.isEmpty() || tipoBeneficio == null || nbDigitado.isEmpty() || codCaixa.isEmpty()) {
            showAlert("Erro", "Todos os campos devem ser preenchidos.", Alert.AlertType.ERROR);
            return;
        }

        // Validação de CPF
        if (!validarCPF(cpfLimpo)) {
            showAlert("Erro", "CPF inválido.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Verificar se o segurado já existe pelo CPF
            Segurado seguradoExistente;
            try {
                seguradoExistente = seguradoService.buscarPorCpfUnico(cpfLimpo);
            } catch (ObjectNotFoundException e) {
                // Se não existir, criar novo segurado
                Segurado novoSegurado = new Segurado();
                novoSegurado.setNomeSegurado(nome);
                novoSegurado.setCpf(cpfLimpo);
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
     * Valida CPF (retirado do seu código).
     */
    private boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");
        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int soma = 0;
        for (int i = 0; i < 9; i++) soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) primeiroDigito = 0;

        soma = 0;
        for (int i = 0; i < 10; i++) soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) segundoDigito = 0;

        return primeiroDigito == Character.getNumericValue(cpf.charAt(9))
                && segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }

    /**
     * Aplica máscara no campo CPF igual ao código do AlterarArquivoController.
     */
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

                int caretPosition = textField.getCaretPosition();
                textField.setText(formatted.toString());
                textField.positionCaret(Math.min(caretPosition, textField.getText().length()));

                changing = false;
            }
        });
    }

    /**
     * Atualiza o campo que mostra o tipo de benefício por extenso.
     */
    private void updateTipoBeneficioField(String tipo) {
        if (tipo == null) {
            viewTipoBeneficioField.setText("Tipo inválido");
            return;
        }
        String descricao = tipoBeneficioMap.getOrDefault(tipo, "Tipo de benefício desconhecido");
        viewTipoBeneficioField.setText(descricao);
    }

    /**
     * Inicializa o mapa dos tipos de benefícios.
     */
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
    /**
     * Limpa os campos da tela após o cadastro.
     * Campos NB e Caixa permanecem preenchidos.
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
     */
    private void showAlert(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
