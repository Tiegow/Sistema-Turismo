package br.com.agencia.model;

import java.time.LocalDateTime;
import java.util.List;

public class Passeio {
    private Integer id;
    private Integer preco;
    private Integer capacidade;
    private LocalDateTime dataHora;
    private Roteiro roteiro; 
    private Integer vagasDisp; // derivado

    private List<Veiculo> veiculosAlocados;
    private List<Colaborador> colaboradoresAlocados;

    public Passeio() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPreco() { return preco; }
    public void setPreco(Integer preco) { this.preco = preco; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Roteiro getRoteiro() { return roteiro; }
    public void setRoteiro(Roteiro roteiro) { this.roteiro = roteiro; }

    public Integer getVagasDisp() { return vagasDisp; }
    public void setVagasDisp(Integer vagasDisp) { this.vagasDisp = vagasDisp; }

    public List<Veiculo> getVeiculosAlocados() { return veiculosAlocados; }
    public void setVeiculosAlocados(List<Veiculo> veiculosAlocados) { this.veiculosAlocados = veiculosAlocados; }

    public List<Colaborador> getColaboradoresAlocados() { return colaboradoresAlocados; }
    public void setColaboradoresAlocados(List<Colaborador> colaboradoresAlocados) { this.colaboradoresAlocados = colaboradoresAlocados; }
}
