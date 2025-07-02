package inss.gca.mogi.mogi.model;

import java.time.LocalDateTime;

/**
 * Classe responsável por representar um log de ação realizada por um servidor no sistema.
 */
public class LogServidor {

    private int id;
    private int servidorId;
    private String acao;
    private LocalDateTime dataHora;
    private String detalhes;

    /**
     * Construtor vazio.
     * Necessário para frameworks de persistência ou serialização.
     */
    public LogServidor() {
    }

    /**
     * Construtor completo para criação de logs.
     *
     * @param servidorId ID do servidor que realizou a ação
     * @param acao       Descrição da ação realizada
     * @param dataHora   Data e hora da ação
     * @param detalhes   Detalhes adicionais da ação (pode ser null)
     */
    public LogServidor(int servidorId, String acao, LocalDateTime dataHora, String detalhes) {
        this.servidorId = servidorId;
        this.acao = acao;
        this.dataHora = dataHora;
        this.detalhes = detalhes;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServidorId() {
        return servidorId;
    }

    public void setServidorId(int servidorId) {
        this.servidorId = servidorId;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }
}
