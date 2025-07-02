package inss.gca.mogi.mogi.dto;
/**
 * DTO que agrega dados do Arquivo mais informações de Segurado e Caixa.
 * Usado em telas de consulta/alteração onde precisamos exibir tudo junto.
 */
public class BuscaDTO {
    private int id;
    private String codCaixa;
    private String nb;
    private String tipoBeneficio;
    private String cpfSegurado;
    private String nomeSegurado;
    private String rua;
    private int prateleira;
    private String andar;
    /* Getters e Setters */
    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }

    public String getCodCaixa()      { return codCaixa; }
    public void setCodCaixa(String codCaixa) { this.codCaixa = codCaixa; }

    public String getNb()            { return nb; }
    public void setNb(String nb)     { this.nb = nb; }

    public String getTipoBeneficio() { return tipoBeneficio; }
    public void setTipoBeneficio(String tipoBeneficio) { this.tipoBeneficio = tipoBeneficio; }

    public String getCpfSegurado()   { return cpfSegurado; }
    public void setCpfSegurado(String cpfSegurado) { this.cpfSegurado = cpfSegurado; }

    public String getNomeSegurado()  { return nomeSegurado; }
    public void setNomeSegurado(String nomeSegurado) { this.nomeSegurado = nomeSegurado; }

    public String getRua()           { return rua; }
    public void setRua(String rua)   { this.rua = rua; }

    public int getPrateleira()    { return prateleira; }
    public void setPrateleira(int prateleira) { this.prateleira = prateleira; }

    public String getAndar()         { return andar; }
    public void setAndar(String andar) { this.andar = andar; }
}
