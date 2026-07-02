package br.com.agencia.model;

import java.time.LocalDateTime;

public class Manutencao {
    private Integer id;
    private String status;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private String motivo;
    private Integer custo;
    private Veiculo veiculo; 

    public Manutencao() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getCusto() { return custo; }
    public void setCusto(Integer custo) { this.custo = custo; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
}
