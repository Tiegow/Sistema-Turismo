package br.com.agencia.service;

import br.com.agencia.dao.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {
    
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final TuristaDAO turistaDAO = new TuristaDAO();
    private final PasseioDAO passeioDAO = new PasseioDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final ManutencaoDAO manutencaoDAO = new ManutencaoDAO();

    public Map<String, Object> obterMetricas() {
        Map<String, Object> metricas = new HashMap<>();
        try {
            metricas.put("totalReceita", reservaDAO.calcularReceitaTotal());
            metricas.put("receitaAno", reservaDAO.calcularReceitaAnoAtual());
            metricas.put("receitaMes", reservaDAO.calcularReceitaMesAtual());
            metricas.put("roteiroMaisVendido", reservaDAO.buscarRoteiroMaisVendido());
            metricas.put("totalTuristas", turistaDAO.contarTotalTuristas());
            metricas.put("passeiosFuturos", passeioDAO.contarPasseiosFuturos());
            
            int totalVeiculos = veiculoDAO.contarTotalVeiculos();
            int emManutencao = manutencaoDAO.contarVeiculosEmManutencao();
            metricas.put("veiculosTotais", totalVeiculos);
            metricas.put("veiculosDisponiveis", totalVeiculos - emManutencao);
            
        } catch (SQLException e) {
            metricas.put("totalReceita", 0.0);
            metricas.put("receitaAno", 0.0);
            metricas.put("receitaMes", 0.0);
            metricas.put("roteiroMaisVendido", "Nenhum");
            metricas.put("totalTuristas", 0);
            metricas.put("passeiosFuturos", 0);
            metricas.put("veiculosTotais", 0);
            metricas.put("veiculosDisponiveis", 0);
        }
        return metricas;
    }
}
