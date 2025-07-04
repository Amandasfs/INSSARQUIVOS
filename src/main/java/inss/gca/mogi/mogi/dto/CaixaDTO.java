package inss.gca.mogi.mogi.dto;

public class CaixaDTO {
    private String codCaixa;
    private int prateleira;
    private String rua;
    private String andar;
    private String nbInicial;
    private String nbFinal;
    private int idServidor;

    // Getters e Setters
    public String getCodCaixa() { return codCaixa; }
    public void setCodCaixa(String codCaixa) { this.codCaixa = codCaixa; }

    public int getPrateleira() { return prateleira; }
    public void setPrateleira(int prateleira) { this.prateleira = prateleira; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getAndar() { return andar; }
    public void setAndar(String andar) { this.andar = andar; }

    public String getNbInicial() { return nbInicial; }
    public void setNbInicial(String nbInicial) { this.nbInicial = nbInicial; }

    public String getNbFinal() { return nbFinal; }
    public void setNbFinal(String nbFinal) { this.nbFinal = nbFinal; }

    public int getIdServidor() { return idServidor; }
    public void setIdServidor(int idServidor) { this.idServidor = idServidor; }
}