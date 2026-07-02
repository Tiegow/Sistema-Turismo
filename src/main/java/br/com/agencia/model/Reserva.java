package br.com.agencia.model;

public class Reserva {
    private Passeio passeio;
    private Turista turista;
    private Integer qtdVagas;
    private Boolean pagamentoEfetuado;
    private Integer valorTotal;
    private String localEmbarque;

    public Reserva() {}

    public Passeio getPasseio() { return passeio; }
    public void setPasseio(Passeio passeio) { this.passeio = passeio; }

    public Turista getTurista() { return turista; }
    public void setTurista(Turista turista) { this.turista = turista; }

    public Integer getQtdVagas() { return qtdVagas; }
    public void setQtdVagas(Integer qtdVagas) { this.qtdVagas = qtdVagas; }

    public Boolean getPagamentoEfetuado() { return pagamentoEfetuado; }
    public void setPagamentoEfetuado(Boolean pagamentoEfetuado) { this.pagamentoEfetuado = pagamentoEfetuado; }

    public Integer getValorTotal() { return valorTotal; }
    public void setValorTotal(Integer valorTotal) { this.valorTotal = valorTotal; }

    public String getLocalEmbarque() { return localEmbarque; }
    public void setLocalEmbarque(String localEmbarque) { this.localEmbarque = localEmbarque; }
}
