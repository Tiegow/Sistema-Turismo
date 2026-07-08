package br.com.agencia.dao;

import br.com.agencia.config.ConnectionFactory;
import br.com.agencia.model.Reserva;
import br.com.agencia.model.Passeio;
import br.com.agencia.model.Turista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public void inserir(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO RESERVA (id_passeio, id_turista, qtd_vagas, pagamento_efetuado, valor_total, local_embarque) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, reserva.getPasseio().getId());
            stmt.setInt(2, reserva.getTurista().getId());
            
            stmt.setInt(3, reserva.getQtdVagas() != null ? reserva.getQtdVagas() : 1);
            stmt.setBoolean(4, reserva.getPagamentoEfetuado() != null ? reserva.getPagamentoEfetuado() : false);
            stmt.setDouble(5, reserva.getValorTotal() != null ? reserva.getValorTotal() : 0.0);
            stmt.setString(6, reserva.getLocalEmbarque());
            
            stmt.executeUpdate();
        }
    }

    public List<Reserva> buscarTodos() throws SQLException {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT id_passeio, id_turista, qtd_vagas, pagamento_efetuado, valor_total, local_embarque FROM RESERVA";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(extrairReserva(rs));
            }
        }
        return lista;
    }

    public List<Reserva> buscarPorPasseio(int idPasseio) throws SQLException {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT id_passeio, id_turista, qtd_vagas, pagamento_efetuado, valor_total, local_embarque " +
                     "FROM RESERVA WHERE id_passeio = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idPasseio);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairReserva(rs));
                }
            }
        }
        return lista;
    }

    public List<Reserva> buscarPorTurista(int idTurista) throws SQLException {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT id_passeio, id_turista, qtd_vagas, pagamento_efetuado, valor_total, local_embarque " +
                     "FROM RESERVA WHERE id_turista = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idTurista);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairReserva(rs));
                }
            }
        }
        return lista;
    }

    public void atualizar(Reserva reserva) throws SQLException {
        String sql = "UPDATE RESERVA SET qtd_vagas = ?, pagamento_efetuado = ?, valor_total = ?, local_embarque = ? " +
                     "WHERE id_passeio = ? AND id_turista = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, reserva.getQtdVagas() != null ? reserva.getQtdVagas() : 1);
            stmt.setBoolean(2, reserva.getPagamentoEfetuado() != null ? reserva.getPagamentoEfetuado() : false);
            stmt.setDouble(3, reserva.getValorTotal() != null ? reserva.getValorTotal() : 0.0);
            stmt.setString(4, reserva.getLocalEmbarque());
            
            stmt.setInt(5, reserva.getPasseio().getId());
            stmt.setInt(6, reserva.getTurista().getId());
            
            stmt.executeUpdate();
        }
    }

    public void deletar(int idPasseio, int idTurista) throws SQLException {
        String sql = "DELETE FROM RESERVA WHERE id_passeio = ? AND id_turista = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idPasseio);
            stmt.setInt(2, idTurista);
            stmt.executeUpdate();
        }
    }

    private Reserva extrairReserva(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        
        Passeio p = new Passeio();
        p.setId(rs.getInt("id_passeio"));
        r.setPasseio(p); 
        
        Turista t = new Turista();
        t.setId(rs.getInt("id_turista"));
        r.setTurista(t); 
        
        r.setQtdVagas(rs.getInt("qtd_vagas"));
        r.setPagamentoEfetuado(rs.getBoolean("pagamento_efetuado"));
        r.setValorTotal(rs.getDouble("valor_total"));
        r.setLocalEmbarque(rs.getString("local_embarque"));
        
        return r;
    }

    public boolean verificarConflitoTurista(int idTurista, java.time.LocalDateTime inicio, java.time.LocalDateTime fim, int idPasseioIgnorado) throws SQLException {
        String sql = "SELECT count(*) as total FROM PASSEIO p " +
                     "JOIN ROTEIRO r ON p.id_roteiro = r.id " +
                     "JOIN RESERVA res ON res.id_passeio = p.id " +
                     "WHERE res.id_turista = ? AND p.id != ? " +
                     "  AND p.data_hora < ? " +
                     "  AND DATE_ADD(p.data_hora, INTERVAL COALESCE(r.duracao, 0) MINUTE) > ?";
                     
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idTurista);
            stmt.setInt(2, idPasseioIgnorado);
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(fim));
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(inicio));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        }
        return false;
    }

    public Double calcularReceitaTotal() throws SQLException {
        String sql = "SELECT SUM(valor_total) as total FROM RESERVA WHERE pagamento_efetuado = 1";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    public Double calcularReceitaAnoAtual() throws SQLException {
        String sql = "SELECT SUM(r.valor_total) as total FROM RESERVA r " +
                     "JOIN PASSEIO p ON r.id_passeio = p.id " +
                     "WHERE r.pagamento_efetuado = 1 AND YEAR(p.data_hora) = YEAR(NOW())";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    public Double calcularReceitaMesAtual() throws SQLException {
        String sql = "SELECT SUM(r.valor_total) as total FROM RESERVA r " +
                     "JOIN PASSEIO p ON r.id_passeio = p.id " +
                     "WHERE r.pagamento_efetuado = 1 AND YEAR(p.data_hora) = YEAR(NOW()) AND MONTH(p.data_hora) = MONTH(NOW())";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    public String buscarRoteiroMaisVendido() throws SQLException {
        String sql = "SELECT r.nome FROM RESERVA res " +
                     "JOIN PASSEIO p ON res.id_passeio = p.id " +
                     "JOIN ROTEIRO r ON p.id_roteiro = r.id " +
                     "GROUP BY r.id, r.nome " +
                     "ORDER BY SUM(res.qtd_vagas) DESC LIMIT 1";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("nome");
            }
        }
        return "Nenhum";
    }
}
