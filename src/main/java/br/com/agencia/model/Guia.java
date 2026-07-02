package br.com.agencia.model;

import java.util.List;

public class Guia extends Colaborador {
    private List<String> idiomas;

    public Guia() {}

    public List<String> getIdiomas() { return idiomas; }
    public void setIdiomas(List<String> idiomas) { this.idiomas = idiomas; }
}
