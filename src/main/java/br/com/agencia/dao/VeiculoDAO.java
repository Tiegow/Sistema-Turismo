package br.com.agencia.dao;

import br.com.agencia.config.ConnectionFactory;
import br.com.agencia.model.Veiculo;
import br.com.agencia.model.Manutencao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    public int inserir(Veiculo veiculo) throws SQLException {
        String sql = "INSERT INTO VEICULO (placa, modelo, capacidade) VALUES (?, ?, ?)";
        int idGerado = -1;

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setInt(3, veiculo.getCapacidade() != null ? veiculo.getCapacidade() : 0);
            
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                    veiculo.setId(idGerado);
                }
            }
        }
        return idGerado;
    }

    public List<Veiculo> buscarTodos() throws SQLException {
        List<Veiculo> lista = new ArrayList<>();
        String sql = "SELECT id, placa, modelo, capacidade FROM VEICULO";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(extrairVeiculo(rs, con));
            }
        }
        return lista;
    }

    public Veiculo buscarPorId(int id) throws SQLException {
        Veiculo veiculo = null;
        String sql = "SELECT id, placa, modelo, capacidade FROM VEICULO WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    veiculo = extrairVeiculo(rs, con);
                }
            }
        }
        return veiculo;
    }

    public void atualizar(Veiculo veiculo) throws SQLException {
        String sql = "UPDATE VEICULO SET placa = ?, modelo = ?, capacidade = ? WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getCapacidade() != null ? String.valueOf(veiculo.getCapacidade()) : "0");
            stmt.setInt(4, veiculo.getId());
            
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM MANUTENCAO WHERE id_veiculo = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM VEICULO_ALOCADO WHERE id_veiculo = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM VEICULO WHERE id = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private Veiculo extrairVeiculo(ResultSet rs, Connection con) throws SQLException {
        Veiculo v = new Veiculo();
        v.setId(rs.getInt("id"));
        v.setPlaca(rs.getString("placa"));
        v.setModelo(rs.getString("modelo"));
        v.setCapacidade(rs.getInt("capacidade"));
        
        v.setHistoricoManutencao(buscarManutencoes(v.getId(), con, v));
        
        return v;
    }

    private List<Manutencao> buscarManutencoes(int idVeiculo, Connection con, Veiculo vPai) throws SQLException {
        List<Manutencao> manutencoes = new ArrayList<>();
        String sql = "SELECT id, status, data_entrada, data_saida, motivo, custo FROM MANUTENCAO WHERE id_veiculo = ? ORDER BY data_entrada DESC";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idVeiculo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Manutencao m = new Manutencao();
                    m.setId(rs.getInt("id"));
                    m.setStatus(rs.getString("status"));
                    m.setMotivo(rs.getString("motivo"));
                    m.setCusto(rs.getInt("custo"));
                    
                    Timestamp de = rs.getTimestamp("data_entrada");
                    if (de != null) m.setDataEntrada(de.toLocalDateTime());
                    
                    Timestamp ds = rs.getTimestamp("data_saida");
                    if (ds != null) m.setDataSaida(ds.toLocalDateTime());
                    
                    m.setVeiculo(vPai); 
                    manutencoes.add(m);
                }
            }
        }
        return manutencoes;
    }
}
