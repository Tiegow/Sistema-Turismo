package br.com.agencia.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class Motorista extends Colaborador {
    private String numeroCnh;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validade;
    private List<String> categoriasCnh;

    public Motorista() {}

    public String getNumeroCnh() { return numeroCnh; }
    public void setNumeroCnh(String numeroCnh) { this.numeroCnh = numeroCnh; }

    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }

    public List<String> getCategoriasCnh() { return categoriasCnh; }
    public void setCategoriasCnh(List<String> categoriasCnh) { this.categoriasCnh = categoriasCnh; }
}
