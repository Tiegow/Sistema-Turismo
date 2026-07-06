package br.com.agencia.model;

import java.util.List;

public class Roteiro {
    private Integer id;
    private String nome;
    private Integer duracao;
    private String modalidade;
    private String descricao;

    private List<RoteiroPreco> historicoPrecos;

    public Roteiro() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getDuracao() { return duracao; }
    public void setDuracao(Integer duracao) { this.duracao = duracao; }

    public String getDuracaoFormatada() {
        if (duracao == null) return "Não informada";
        if (duracao >= 1440 && duracao % 1440 == 0) return (duracao / 1440) + " Dia(s)";
        if (duracao >= 60 && duracao % 60 == 0) return (duracao / 60) + " Hora(s)";
        return duracao + " Minutos";
    }

    public String getModalidade() { return modalidade; }
    public void setModalidade(String modalidade) { this.modalidade = modalidade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<RoteiroPreco> getHistoricoPrecos() { return historicoPrecos; }
    public void setHistoricoPrecos(List<RoteiroPreco> historicoPrecos) { this.historicoPrecos = historicoPrecos; }
}
