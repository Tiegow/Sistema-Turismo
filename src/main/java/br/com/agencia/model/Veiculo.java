package br.com.agencia.model;

import java.util.List;

public class Veiculo {
    private Integer id;
    private String placa;
    private String modelo;
    private Integer capacidade;

    private List<Manutencao> historicoManutencao;

    public Veiculo() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    public List<Manutencao> getHistoricoManutencao() { return historicoManutencao; }
    public void setHistoricoManutencao(List<Manutencao> historicoManutencao) { this.historicoManutencao = historicoManutencao; }
}
