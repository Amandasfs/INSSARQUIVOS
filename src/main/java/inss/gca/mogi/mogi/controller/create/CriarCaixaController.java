package inss.gca.mogi.mogi.controller.create;

import inss.gca.mogi.mogi.model.Caixa;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.CaixaService;
import inss.gca.mogi.mogi.util.AlertaUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.math.BigInteger;

/**
 * Controller responsável pela interface de criação de novas caixas.
 */
public class CriarCaixaController {

    @FXML private TextField codCaixaField;
    @FXML private TextField ruaField;
    @FXML private TextField prateleiraField;
    @FXML private TextField nbInicialField;
    @FXML private TextField nbFinalField;
    @FXML private ChoiceBox<String> andarSelect;
    @FXML private Button cadastrarButton;

    private final CaixaService caixaService = new CaixaService();

    // Simulação de usuário logado, ajuste para obter do contexto real
    private final int idServidorLogado = 1;
    private final String perfilUsuarioLogado = "GERENTE";

    @FXML
    public void initialize() {
        configurarCampos();
        configurarBotaoCadastrar();
    }

    private void configurarCampos() {
        ruaField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 2) ruaField.setText(oldVal);
        });

        prateleiraField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 4) prateleiraField.setText(oldVal);
        });

        codCaixaField.textProperty().addListener((obs, oldVal, newVal) -> {
            String formatted = formatCodCaixa(newVal);
            if (!codCaixaField.getText().equals(formatted)) {
                codCaixaField.setText(formatted);
                codCaixaField.positionCaret(formatted.length());
            }
        });

        nbInicialField.textProperty().addListener((obs, oldVal, newVal) -> {
            String formatted = formatNb(newVal);
            if (!nbInicialField.getText().equals(formatted)) {
                nbInicialField.setText(formatted);
                nbInicialField.positionCaret(formatted.length());
            }
        });

        nbFinalField.textProperty().addListener((obs, oldVal, newVal) -> {
            String formatted = formatNb(newVal);
            if (!nbFinalField.getText().equals(formatted)) {
                nbFinalField.setText(formatted);
                nbFinalField.positionCaret(formatted.length());
            }
        });

        andarSelect.getItems().setAll("Andar", "segundo", "terceiro");
        andarSelect.getSelectionModel().selectFirst();
    }

    private void configurarBotaoCadastrar() {
        cadastrarButton.setDisable(true);

        andarSelect.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            cadastrarButton.setDisable(!"segundo".equalsIgnoreCase(newVal) && !"terceiro".equalsIgnoreCase(newVal));
        });

        cadastrarButton.setOnAction(event -> cadastrarCaixa());
    }

    private void cadastrarCaixa() {
        try {
            // Valida se perfil está ativo e tem permissão para cadastrar
            PermissaoValidator.validarPerfilAtivo(idServidorLogado);
            PermissaoValidator.validarPodeCadastrar(idServidorLogado);

            String codCaixa = codCaixaField.getText().trim();
            String rua = ruaField.getText().trim();
            String prateleiraStr = prateleiraField.getText().trim();
            String nbInicialRaw = nbInicialField.getText().trim();
            String nbFinalRaw = nbFinalField.getText().trim();
            String andar = andarSelect.getValue();

            if (codCaixa.isEmpty() || rua.isEmpty() || prateleiraStr.isEmpty() ||
                    nbInicialRaw.isEmpty() || nbFinalRaw.isEmpty() ||
                    andar == null || andar.equals("Andar")) {
                AlertaUtil.exibirErro("Preencha todos os campos corretamente.");
                return;
            }

            int prateleira;
            try {
                prateleira = Integer.parseInt(prateleiraStr);
            } catch (NumberFormatException e) {
                AlertaUtil.exibirErro("Prateleira deve ser um número válido.");
                return;
            }

            String nbInicialNumerico = nbInicialRaw.replaceAll("[^\\d]", "");
            String nbFinalNumerico = nbFinalRaw.replaceAll("[^\\d]", "");

            if (nbInicialNumerico.isEmpty() || nbFinalNumerico.isEmpty()) {
                AlertaUtil.exibirErro("NB inicial e final devem conter números válidos.");
                return;
            }

            BigInteger nbInicial = new BigInteger(nbInicialNumerico);
            BigInteger nbFinal = new BigInteger(nbFinalNumerico);

            if (nbInicial.compareTo(nbFinal) >= 0) {
                AlertaUtil.exibirErro("NB inicial deve ser menor que NB final e eles não podem ser iguais.");
                return;
            }

            Caixa caixa = new Caixa();
            caixa.setCodCaixa(codCaixa);
            caixa.setRua(rua);
            caixa.setPrateleira(prateleira);
            caixa.setNbInicial(nbInicialNumerico);
            caixa.setNbFinal(nbFinalNumerico);
            caixa.setAndar(andar);
            caixa.setIdServidor(idServidorLogado);

            // Usa service com idServidor e perfil para validar permissões
            caixaService.cadastrarCaixa(caixa, perfilUsuarioLogado, idServidorLogado);

            AlertaUtil.exibirInformacao("Caixa cadastrada com sucesso!");

            codCaixaField.clear();
            ruaField.clear();
            prateleiraField.clear();
            nbInicialField.clear();
            nbFinalField.clear();
            andarSelect.getSelectionModel().selectFirst();

        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.exibirErro("Erro ao cadastrar caixa: " + e.getMessage());
        }
    }

    private String formatCodCaixa(String raw) {
        raw = raw.replaceAll("[^\\d]", "");
        if (raw.isEmpty()) return "CX0000";
        try {
            return String.format("CX%04d", Integer.parseInt(raw));
        } catch (NumberFormatException e) {
            return "CX0000";
        }
    }

    private String formatNb(String raw) {
        raw = raw.replaceAll("[^\\d]", "");
        StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < raw.length() && i < 10; i++) {
            formatted.append(raw.charAt(i));
            if (i == 2 || i == 5) formatted.append('.');
            if (i == 8) formatted.append('-');
        }
        return formatted.toString();
    }
}
