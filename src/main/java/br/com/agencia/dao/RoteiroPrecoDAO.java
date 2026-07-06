package br.com.agencia.dao;

import br.com.agencia.config.ConnectionFactory;
import br.com.agencia.model.RoteiroPreco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class RoteiroPrecoDAO {

    public void inserir(RoteiroPreco preco, int idRoteiro) throws SQLException {
        String sql = "INSERT INTO ROTEIRO_PRECO (id_roteiro, data_cadastro, ativo, preco) VALUES (?, ?, ?, ?)";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, idRoteiro);
            
            LocalDateTime dataCadastro = preco.getDataCadastro() != null ? preco.getDataCadastro() : LocalDateTime.now();
            stmt.setTimestamp(2, Timestamp.valueOf(dataCadastro));
            
            stmt.setBoolean(3, preco.getAtivo() != null ? preco.getAtivo() : true);
            stmt.setDouble(4, preco.getPreco() != null ? preco.getPreco() : 0.0);
            
            stmt.executeUpdate();
        }
    }

    public void inativarPrecoAntigos(int idRoteiro) throws SQLException {
        String sql = "UPDATE ROTEIRO_PRECO SET ativo = 0 WHERE id_roteiro = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, idRoteiro);
            stmt.executeUpdate();
        }
    }

    public RoteiroPreco buscarPrecoAtivo(int idRoteiro) throws SQLException {
        RoteiroPreco preco = null;
        String sql = "SELECT id, data_cadastro, ativo, preco FROM ROTEIRO_PRECO WHERE id_roteiro = ? AND ativo = 1 ORDER BY data_cadastro DESC LIMIT 1";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setInt(1, idRoteiro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    preco = new RoteiroPreco();
                    preco.setId(rs.getInt("id"));
                    preco.setAtivo(rs.getBoolean("ativo"));
                    preco.setPreco(rs.getDouble("preco"));
                    
                    Timestamp data = rs.getTimestamp("data_cadastro");
                    if (data != null) {
                        preco.setDataCadastro(data.toLocalDateTime());
                    }
                }
            }
        }
        return preco;
    }
}
