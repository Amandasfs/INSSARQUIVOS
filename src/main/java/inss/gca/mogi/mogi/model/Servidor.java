package inss.gca.mogi.mogi.model;

public class Servidor {
    private int id;
    private String nome;
    private String senha;
    private int matricula;
    private TipoPerfil tipoPerfil;
    private boolean statusPerfil;
    private boolean podeCadastrar;
    private boolean podeAlterar;
    private boolean podeAlterarNomeSegurado;
    private boolean podeAlterarCaixa;
    private boolean podeAlterarCpfNb;
    private boolean podeAlterarLocalCaixa;
    private boolean podeAlterarCodigoCaixa;
    private boolean podeExcluir;

    public Servidor(int id, String nome, String senha, int matricula, TipoPerfil tipoPerfil) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.matricula = matricula;
        this.tipoPerfil = tipoPerfil;
    }

    public Servidor(boolean statusPerfil, boolean podeCadastrar, boolean podeAlterar, boolean podeAlterarNomeSegurado, boolean podeAlterarCaixa, boolean podeAlterarCpfNb, boolean podeAlterarLocalCaixa, boolean podeAlterarCodigoCaixa, boolean podeExcluir) {
        this.statusPerfil = statusPerfil;
        this.podeCadastrar = podeCadastrar;
        this.podeAlterar = podeAlterar;
        this.podeAlterarNomeSegurado = podeAlterarNomeSegurado;
        this.podeAlterarCaixa = podeAlterarCaixa;
        this.podeAlterarCpfNb = podeAlterarCpfNb;
        this.podeAlterarLocalCaixa = podeAlterarLocalCaixa;
        this.podeAlterarCodigoCaixa = podeAlterarCodigoCaixa;
        this.podeExcluir = podeExcluir;
    }

    public Servidor() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public TipoPerfil getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    public boolean isStatusPerfil() {
        return statusPerfil;
    }

    public void setStatusPerfil(boolean statusPerfil) {
        this.statusPerfil = statusPerfil;
    }

    public boolean isPodeCadastrar() {
        return podeCadastrar;
    }

    public void setPodeCadastrar(boolean podeCadastrar) {
        this.podeCadastrar = podeCadastrar;
    }

    public boolean isPodeAlterar() {
        return podeAlterar;
    }

    public void setPodeAlterar(boolean podeAlterar) {
        this.podeAlterar = podeAlterar;
    }

    public boolean isPodeAlterarNomeSegurado() {
        return podeAlterarNomeSegurado;
    }

    public void setPodeAlterarNomeSegurado(boolean podeAlterarNomeSegurado) {
        this.podeAlterarNomeSegurado = podeAlterarNomeSegurado;
    }

    public boolean isPodeAlterarCaixa() {
        return podeAlterarCaixa;
    }

    public void setPodeAlterarCaixa(boolean podeAlterarCaixa) {
        this.podeAlterarCaixa = podeAlterarCaixa;
    }

    public boolean isPodeAlterarCpfNb() {
        return podeAlterarCpfNb;
    }

    public void setPodeAlterarCpfNb(boolean podeAlterarCpfNb) {
        this.podeAlterarCpfNb = podeAlterarCpfNb;
    }

    public boolean isPodeAlterarLocalCaixa() {
        return podeAlterarLocalCaixa;
    }

    public void setPodeAlterarLocalCaixa(boolean podeAlterarLocalCaixa) {
        this.podeAlterarLocalCaixa = podeAlterarLocalCaixa;
    }

    public boolean isPodeAlterarCodigoCaixa() {
        return podeAlterarCodigoCaixa;
    }

    public void setPodeAlterarCodigoCaixa(boolean podeAlterarCodigoCaixa) {
        this.podeAlterarCodigoCaixa = podeAlterarCodigoCaixa;
    }

    public boolean isPodeExcluir() {
        return podeExcluir;
    }

    public void setPodeExcluir(boolean podeExcluir) {
        this.podeExcluir = podeExcluir;
    }

    public boolean isGerente() {
        return this.tipoPerfil == TipoPerfil.GERENTE;
    }

}
