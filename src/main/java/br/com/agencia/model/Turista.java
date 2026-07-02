package br.com.agencia.model;

import java.util.List;

public class Turista extends Pessoa {
    private String formaPagamento;
    private List<Acompanhante> acompanhantes;

    public Turista() {}

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public List<Acompanhante> getAcompanhantes() { return acompanhantes; }
    public void setAcompanhantes(List<Acompanhante> acompanhantes) { this.acompanhantes = acompanhantes; }
}
