package br.com.agencia.model;

public class Pessoa {
    private Integer id;
    private String identificacao;
    private String nome;
    private String email;
    private String telefone;

    public Pessoa() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getIdentificacao() { return identificacao; }
    public void setIdentificacao(String identificacao) { this.identificacao = identificacao; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
