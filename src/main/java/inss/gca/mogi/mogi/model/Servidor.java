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

    // Construtor principal - melhorado
    public Servidor(int id, String nome, String senha, int matricula, TipoPerfil tipoPerfil) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.matricula = matricula;
        this.setTipoPerfil(tipoPerfil); // Chama definirPermissoesPadrao() via setter
    }

    // Construtor de permissões - agora chama definirPermissoesPadrao()
    public Servidor(boolean statusPerfil, boolean podeCadastrar, boolean podeAlterar,
                    boolean podeAlterarNomeSegurado, boolean podeAlterarCaixa,
                    boolean podeAlterarCpfNb, boolean podeAlterarLocalCaixa,
                    boolean podeAlterarCodigoCaixa, boolean podeExcluir) {
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

    public Servidor() {}

    // Método mais robusto para definir permissões
    public void definirPermissoesPadrao() {
        if (this.tipoPerfil == null) {
            this.resetarPermissoes();
            return;
        }

        switch (this.tipoPerfil) {
            case GERENTE:
                this.concederTodasPermissoes();
                break;
            case ARQUIVISTA:
                this.resetarPermissoes();
                this.podeCadastrar = true; // Permissão mínima padrão
                break;
            default:
                this.resetarPermissoes();
        }
    }

    private void resetarPermissoes() {
        this.podeCadastrar = false;
        this.podeAlterar = false;
        this.podeExcluir = false;
        this.podeAlterarNomeSegurado = false;
        this.podeAlterarCaixa = false;
        this.podeAlterarCpfNb = false;
        this.podeAlterarLocalCaixa = false;
        this.podeAlterarCodigoCaixa = false;
    }

    private void concederTodasPermissoes() {
        this.podeCadastrar = true;
        this.podeAlterar = true;
        this.podeExcluir = true;
        this.podeAlterarNomeSegurado = true;
        this.podeAlterarCaixa = true;
        this.podeAlterarCpfNb = true;
        this.podeAlterarLocalCaixa = true;
        this.podeAlterarCodigoCaixa = true;
    }

    // Método de verificação de permissão otimizado
    public boolean temPermissao(String acao) {
        if (this.isGerente()) return true;

        try {
            return (boolean) this.getClass()
                    .getMethod("is" + acao)
                    .invoke(this);
        } catch (Exception e) {
            return false;
        }
    }

    // Setters estratégicos
    public void setTipoPerfil(TipoPerfil tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
        this.definirPermissoesPadrao();
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

    // Método de verificação de gerente
    public boolean isGerente() {
        return TipoPerfil.GERENTE.equals(this.tipoPerfil);
    }
}
