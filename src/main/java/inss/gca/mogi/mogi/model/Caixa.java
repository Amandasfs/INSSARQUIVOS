package inss.gca.mogi.mogi.model;

public class Caixa {
    private String codCaixa;
    private int prateleira;
    private String rua;
    private String andar;
    private String nbInicial;
    private String nbFinal;
    private int idServidor;

    public Caixa(String codCaixa, int prateleira, String rua, String andar, String nbInicial, String nbFinal, int idServidor) {
        this.codCaixa = codCaixa;
        this.prateleira = prateleira;
        this.rua = rua;
        this.andar = andar;
        this.nbInicial = nbInicial;
        this.nbFinal = nbFinal;
        this.idServidor = idServidor;
    }
    public Caixa(String codCaixa, int prateleira, String rua, String andar, String nbInicial, String nbFinal) {
        this.codCaixa = codCaixa;
        this.prateleira = prateleira;
        this.rua = rua;
        this.andar = andar;
        this.nbInicial = nbInicial;
        this.nbFinal = nbFinal;
    }

    public Caixa() {

    }

    public String getCodCaixa() {
        return codCaixa;
    }

    public void setCodCaixa(String codCaixa) {
        this.codCaixa = codCaixa;
    }

    public int getPrateleira() {
        return prateleira;
    }

    public void setPrateleira(int prateleira) {
        this.prateleira = prateleira;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getAndar() {
        return andar;
    }

    public void setAndar(String andar) {
        this.andar = andar;
    }

    public String getNbInicial() {
        return nbInicial;
    }

    public void setNbInicial(String nbInicial) {
        this.nbInicial = nbInicial;
    }

    public String getNbFinal() {
        return nbFinal;
    }

    public void setNbFinal(String nbFinal) {
        this.nbFinal = nbFinal;
    }

    public int getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(int idServidor) {
        this.idServidor = idServidor;
    }
}
