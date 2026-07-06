package br.com.agencia.service;

import br.com.agencia.dao.ManutencaoDAO;
import br.com.agencia.dao.VeiculoDAO;
import br.com.agencia.model.Manutencao;
import br.com.agencia.model.Veiculo;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class VeiculoService {
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final ManutencaoDAO manutencaoDAO = new ManutencaoDAO();

    public List<Veiculo> listarTodos() {
        try {
            return veiculoDAO.buscarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar veículos.", e);
        }
    }

    public Veiculo buscarPorId(int id) {
        try {
            return veiculoDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar veículo.", e);
        }
    }

    public void salvar(Veiculo veiculo) {
        try {
            if (veiculo.getId() == null || veiculo.getId() == 0) {
                veiculoDAO.inserir(veiculo);
            } else {
                veiculoDAO.atualizar(veiculo);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar veículo! Verifique se a placa já não está cadastrada.", e);
        }
    }

    public void deletar(int id) {
        try {
            veiculoDAO.deletar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao apagar veículo. Ele pode possuir histórico de manutenções ou passeios.", e);
        }
    }

    // --- MANUTENÇÕES ---
    public Manutencao buscarManutencaoPorId(int idManutencao) {
        try {
            return manutencaoDAO.buscarPorId(idManutencao);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar manutenção.", e);
        }
    }

    public void salvarManutencao(Manutencao m, int idVeiculo) {
        if (m.getStatus() == null || m.getStatus().isBlank()) {
            throw new IllegalArgumentException("Status é obrigatório.");
        }
        if (m.getDataEntrada() == null) {
            throw new IllegalArgumentException("Data de entrada é obrigatória.");
        }
        if (m.getDataSaida() != null && m.getDataSaida().isBefore(m.getDataEntrada())) {
            throw new IllegalArgumentException("Data de saída não pode ser anterior à de entrada.");
        }

        try {
            if (m.getId() == null || m.getId() == 0) {
                manutencaoDAO.inserir(m, idVeiculo);
            } else {
                manutencaoDAO.atualizar(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar manutenção no banco.", e);
        }
    }

    public void deletarManutencao(int idManutencao) {
        try {
            manutencaoDAO.deletar(idManutencao);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao apagar manutenção.", e);
        }
    }
}
