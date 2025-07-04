package inss.gca.mogi.mogi.model;

public class Segurado {
    private int id;
    private String nomeSegurado;
    private String cpf;
    private int idServidor;

    public Segurado(int id, String nomeSegurado, String cpf, int idServidor) {
        this.id = id;
        this.nomeSegurado = nomeSegurado;
        this.cpf = cpf;
        this.idServidor = idServidor;
    }

    public Segurado() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeSegurado() {
        return nomeSegurado;
    }

    public void setNomeSegurado(String nomeSegurado) {
        this.nomeSegurado = nomeSegurado;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(int idServidor) {
        this.idServidor = idServidor;
    }

}
