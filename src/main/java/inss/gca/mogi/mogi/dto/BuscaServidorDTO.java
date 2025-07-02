package inss.gca.mogi.mogi.dto;

import java.time.LocalDateTime;

public class BuscaServidorDTO {

    private String tipoAcao;           // Ex: "CADASTRAR_ARQUIVO"
    private LocalDateTime dataHora;    // Data e hora da ação
    private String descricao;          // Ex: "Arquivo NB 123456789 cadastrado"

    public BuscaServidorDTO(String tipoAcao, LocalDateTime dataHora, String descricao) {
        this.tipoAcao = tipoAcao;
        this.dataHora = dataHora;
        this.descricao = descricao;
    }

    public String getTipoAcao() {
        return tipoAcao;
    }

    public void setTipoAcao(String tipoAcao) {
        this.tipoAcao = tipoAcao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
