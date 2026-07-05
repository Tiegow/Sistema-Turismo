package br.com.agencia.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class Colaborador extends Pessoa {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataContratacao;
    private Boolean pj;

    public Colaborador() {}

    public LocalDate getDataContratacao() { return dataContratacao; }
    public void setDataContratacao(LocalDate dataContratacao) { this.dataContratacao = dataContratacao; }

    public Boolean getPj() { return pj; }
    public void setPj(Boolean pj) { this.pj = pj; }
}
