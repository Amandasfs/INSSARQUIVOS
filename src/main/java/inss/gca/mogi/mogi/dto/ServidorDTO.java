package inss.gca.mogi.mogi.dto;

import inss.gca.mogi.mogi.model.TipoPerfil;

public class ServidorDTO {
    private int id;
    private String nome;
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

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getMatricula() { return matricula; }
    public void setMatricula(int matricula) { this.matricula = matricula; }

    public TipoPerfil getTipoPerfil() { return tipoPerfil; }
    public void setTipoPerfil(TipoPerfil tipoPerfil) { this.tipoPerfil = tipoPerfil; }

    public boolean isStatusPerfil() { return statusPerfil; }
    public void setStatusPerfil(boolean statusPerfil) { this.statusPerfil = statusPerfil; }

    // Permissões
    public boolean isPodeCadastrar() { return podeCadastrar; }
    public void setPodeCadastrar(boolean podeCadastrar) { this.podeCadastrar = podeCadastrar; }

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

    // Método auxiliar para verificação no front-end
    public boolean isGerente() {
        return tipoPerfil != null && tipoPerfil.equals(TipoPerfil.GERENTE);
    }
}