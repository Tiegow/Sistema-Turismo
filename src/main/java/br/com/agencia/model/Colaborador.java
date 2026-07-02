package br.com.agencia.model;

import java.time.LocalDateTime;

public class Colaborador extends Pessoa {
    private LocalDateTime dataContratacao;
    private Boolean pj;

    public Colaborador() {}

    public LocalDateTime getDataContratacao() { return dataContratacao; }
    public void setDataContratacao(LocalDateTime dataContratacao) { this.dataContratacao = dataContratacao; }

    public Boolean getPj() { return pj; }
    public void setPj(Boolean pj) { this.pj = pj; }
}
