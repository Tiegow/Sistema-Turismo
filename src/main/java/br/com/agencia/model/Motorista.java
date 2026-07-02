package br.com.agencia.model;

import java.time.LocalDateTime;
import java.util.List;

public class Motorista extends Colaborador {
    private Integer numeroCnh;
    private LocalDateTime validade;
    private List<String> categoriasCnh;

    public Motorista() {}

    public Integer getNumeroCnh() { return numeroCnh; }
    public void setNumeroCnh(Integer numeroCnh) { this.numeroCnh = numeroCnh; }

    public LocalDateTime getValidade() { return validade; }
    public void setValidade(LocalDateTime validade) { this.validade = validade; }

    public List<String> getCategoriasCnh() { return categoriasCnh; }
    public void setCategoriasCnh(List<String> categoriasCnh) { this.categoriasCnh = categoriasCnh; }
}
