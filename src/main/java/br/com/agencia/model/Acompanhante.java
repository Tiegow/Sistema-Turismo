package br.com.agencia.model;

public class Acompanhante {
    private Integer id;
    private String nome;
    private String telefone;
    private Turista turista;

    public Acompanhante() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public Turista getTurista() { return turista; }
    public void setTurista(Turista turista) { this.turista = turista; }
}
