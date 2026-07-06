package br.com.agencia.model;

import java.time.LocalDateTime;
import java.util.List;

public class Passeio {
    private Integer id;
    private Double preco;
    private Integer capacidade;
    private LocalDateTime dataHora;
    private Roteiro roteiro; 
    private Integer vagasDisp; // derivado

    private List<Veiculo> veiculosAlocados;
    private List<Colaborador> colaboradoresAlocados;
    private Boolean alertaMotorista;
    private Boolean alertaGuia;

    public Passeio() {}

    public Boolean getAlertaMotorista() { return alertaMotorista; }
    public void setAlertaMotorista(Boolean alertaMotorista) { this.alertaMotorista = alertaMotorista; }

    public Boolean getAlertaGuia() { return alertaGuia; }
    public void setAlertaGuia(Boolean alertaGuia) { this.alertaGuia = alertaGuia; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

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
