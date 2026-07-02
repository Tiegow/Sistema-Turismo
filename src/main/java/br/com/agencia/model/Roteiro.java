package br.com.agencia.model;

import java.util.List;

public class Roteiro {
    private Integer id;
    private String nome;
    private String duracao;
    private String modalidade;
    private String descricao;

    private List<RoteiroPreco> historicoPrecos;

    public Roteiro() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDuracao() { return duracao; }
    public void setDuracao(String duracao) { this.duracao = duracao; }

    public String getModalidade() { return modalidade; }
    public void setModalidade(String modalidade) { this.modalidade = modalidade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<RoteiroPreco> getHistoricoPrecos() { return historicoPrecos; }
    public void setHistoricoPrecos(List<RoteiroPreco> historicoPrecos) { this.historicoPrecos = historicoPrecos; }
}
