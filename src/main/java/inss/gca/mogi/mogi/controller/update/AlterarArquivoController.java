package inss.gca.mogi.mogi.controller.update;

import inss.gca.mogi.mogi.dao.LogServidorDAO;
import inss.gca.mogi.mogi.dto.BuscaDTO;
import inss.gca.mogi.mogi.model.Arquivo;
import inss.gca.mogi.mogi.model.LogServidor;
import inss.gca.mogi.mogi.security.PermissaoValidator;
import inss.gca.mogi.mogi.service.ArquivoService;
import inss.gca.mogi.mogi.service.SeguradoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.time.LocalDateTime;

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

    public void initData(Arquivo arquivo, int idServidor, String perfilServidor) {
        this.arquivoOriginal = arquivo;
        this.idServidor = idServidor;

        // Inicializa ChoiceBox de tipos de benefício (exemplo)
        tipoSelect.setItems(FXCollections.observableArrayList("INSS", "Pensão", "Aposentadoria"));
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
        aps.setText("Mogi Das Cruzes");  // Campo fixo, pode alterar se quiser
        viewTipoBeneficioField.setText(arquivo.getTipoBeneficio());
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

        // tipoSelect e aps pode ficar sempre editável, ou você bloqueia conforme permissão se quiser

        cadastrarButton.setText("SALVAR ALTERAÇÕES");
    }

    @FXML
    private void cadastrar(ActionEvent event) {
        try {
            boolean alterouAlgo = false;

            // Atualiza NB e CPF
            if (!nb.isDisabled() && !nb.getText().equals(arquivoOriginal.getNb())) {
                arquivoService.atualizarNb(arquivoOriginal.getId(), nb.getText());
                registrarLog("Alteração de NB do arquivo", "De: " + arquivoOriginal.getNb() + " Para: " + nb.getText());
                alterouAlgo = true;
            }
            if (!cpf.isDisabled() && !cpf.getText().equals(buscarCpfPorIdSegurado(arquivoOriginal.getIdSegurado()))) {
                seguradoService.atualizarCPF(arquivoOriginal.getIdSegurado(), cpf.getText());
                registrarLog("Alteração de CPF do segurado", "De: " + buscarCpfPorIdSegurado(arquivoOriginal.getIdSegurado()) + " Para: " + cpf.getText());
                alterouAlgo = true;
            }

            // Atualiza nome do segurado
            if (!nomeSegurado.isDisabled() && !nomeSegurado.getText().equals(buscarNomePorIdSegurado(arquivoOriginal.getIdSegurado()))) {
                seguradoService.atualizarNome(arquivoOriginal.getIdSegurado(), nomeSegurado.getText());
                registrarLog("Alteração de nome do segurado", "De: " + buscarNomePorIdSegurado(arquivoOriginal.getIdSegurado()) + " Para: " + nomeSegurado.getText());
                alterouAlgo = true;
            }

            // Atualiza código da caixa
            if (!numCaixa.isDisabled() && !numCaixa.getText().equals(arquivoOriginal.getCodCaixa())) {
                arquivoService.atualizarCaixa(arquivoOriginal.getId(), numCaixa.getText());
                registrarLog("Alteração de código da caixa no arquivo", "De: " + arquivoOriginal.getCodCaixa() + " Para: " + numCaixa.getText());
                alterouAlgo = true;
            }

            // Atualiza tipo benefício (se quiser permitir)
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

        // Inicializa ChoiceBox com os tipos disponíveis
        tipoSelect.setItems(FXCollections.observableArrayList("INSS", "Pensão", "Aposentadoria"));
        tipoSelect.getSelectionModel().select(dto.getTipoBeneficio());

        // Preenche os campos com os dados vindos do DTO
        nb.setText(dto.getNb());
        cpf.setText(dto.getCpfSegurado());
        nomeSegurado.setText(dto.getNomeSegurado());
        numCaixa.setText(dto.getCodCaixa());
        aps.setText(dto.getRua() + " - Andar " + dto.getAndar() + ", Prateleira " + dto.getPrateleira());
        viewTipoBeneficioField.setText(dto.getTipoBeneficio());

        // Cria um objeto Arquivo "falso" apenas com campos necessários
        this.arquivoOriginal = new Arquivo();
        arquivoOriginal.setId(dto.getId());
        arquivoOriginal.setNb(dto.getNb());
        arquivoOriginal.setTipoBeneficio(dto.getTipoBeneficio());
        arquivoOriginal.setCodCaixa(dto.getCodCaixa());
        arquivoOriginal.setIdServidor(idServidor);

        // Como não temos idSegurado no BuscaDTO, você pode buscar pelo CPF (opcional)
        try {
            int idSegurado = seguradoService.buscarPorCpfUnico(dto.getCpfSegurado()).getId();
            arquivoOriginal.setIdSegurado(idSegurado);
        } catch (Exception e) {
            exibirAlerta("Aviso", "Não foi possível localizar o segurado pelo CPF. Algumas edições podem não funcionar.");
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
}
