package br.com.agencia.model;

import java.time.LocalDateTime;

public class RoteiroPreco {
    private Integer id;
    private Roteiro roteiro;
    private LocalDateTime dataCadastro;
    private Boolean ativo;
    private Double preco;

    public RoteiroPreco() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Roteiro getRoteiro() { return roteiro; }
    public void setRoteiro(Roteiro roteiro) { this.roteiro = roteiro; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
}
