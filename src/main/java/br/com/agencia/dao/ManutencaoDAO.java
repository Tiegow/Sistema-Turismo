package br.com.agencia.dao;

import br.com.agencia.config.ConnectionFactory;
import br.com.agencia.model.Manutencao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ManutencaoDAO {

    public Manutencao buscarPorId(int id) throws SQLException {
        Manutencao m = null;
        String sql = "SELECT id, status, data_entrada, data_saida, motivo, custo FROM MANUTENCAO WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    m = new Manutencao();
                    m.setId(rs.getInt("id"));
                    m.setStatus(rs.getString("status"));
                    m.setMotivo(rs.getString("motivo"));
                    m.setCusto(rs.getDouble("custo"));

                    Timestamp de = rs.getTimestamp("data_entrada");
                    if (de != null) m.setDataEntrada(de.toLocalDateTime());

                    Timestamp ds = rs.getTimestamp("data_saida");
                    if (ds != null) m.setDataSaida(ds.toLocalDateTime());
                }
            }
        }
        return m;
    }

    public void inserir(Manutencao m, int idVeiculo) throws SQLException {
        String sql = "INSERT INTO MANUTENCAO (status, data_entrada, data_saida, motivo, custo, id_veiculo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, m.getStatus());
            
            LocalDateTime de = m.getDataEntrada() != null ? m.getDataEntrada() : LocalDateTime.now();
            stmt.setTimestamp(2, Timestamp.valueOf(de));
            
            stmt.setTimestamp(3, m.getDataSaida() != null ? Timestamp.valueOf(m.getDataSaida()) : null);
            stmt.setString(4, m.getMotivo());
            stmt.setDouble(5, m.getCusto() != null ? m.getCusto() : 0.0);
            stmt.setInt(6, idVeiculo);
            
            stmt.executeUpdate();
        }
    }

    public void atualizar(Manutencao m) throws SQLException {
        String sql = "UPDATE MANUTENCAO SET status = ?, data_entrada = ?, data_saida = ?, motivo = ?, custo = ? WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, m.getStatus());
            
            LocalDateTime de = m.getDataEntrada() != null ? m.getDataEntrada() : LocalDateTime.now();
            stmt.setTimestamp(2, Timestamp.valueOf(de));
            
            stmt.setTimestamp(3, m.getDataSaida() != null ? Timestamp.valueOf(m.getDataSaida()) : null);
            stmt.setString(4, m.getMotivo());
            stmt.setDouble(5, m.getCusto() != null ? m.getCusto() : 0.0);
            stmt.setInt(6, m.getId());
            
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM MANUTENCAO WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
