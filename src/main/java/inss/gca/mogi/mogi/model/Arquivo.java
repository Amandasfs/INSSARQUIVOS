package inss.gca.mogi.mogi.model;

public class Arquivo {
    private int id;
    private String nb;
    private String tipoBeneficio;
    private int idSegurado;
    private String codCaixa;
    private int idServidor;

    public Arquivo(int id, String nb, String tipoBeneficio, int idSegurado, String codCaixa, int idServidor) {
        this.id = id;
        this.nb = nb;
        this.tipoBeneficio = tipoBeneficio;
        this.idSegurado = idSegurado;
        this.codCaixa = codCaixa;
        this.idServidor = idServidor;
    }

    public Arquivo() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNb() {
        return nb;
    }

    public void setNb(String nb) {
        this.nb = nb;
    }

    public String getTipoBeneficio() {
        return tipoBeneficio;
    }

    public void setTipoBeneficio(String tipoBeneficio) {
        this.tipoBeneficio = tipoBeneficio;
    }

    public int getIdSegurado() {
        return idSegurado;
    }

    public void setIdSegurado(int idSegurado) {
        this.idSegurado = idSegurado;
    }

    public String getCodCaixa() {
        return codCaixa;
    }

    public void setCodCaixa(String codCaixa) {
        this.codCaixa = codCaixa;
    }

    public int getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(int idServidor) {
        this.idServidor = idServidor;
    }
}
