package br.com.agencia.dao;

import br.com.agencia.config.ConnectionFactory;
import br.com.agencia.model.Roteiro;
import br.com.agencia.model.RoteiroPreco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RoteiroDAO {

    public int inserir(Roteiro roteiro) throws SQLException {
        String sql = "INSERT INTO ROTEIRO (nome, duracao, modalidade, descricao) VALUES (?, ?, ?, ?)";
        int idGerado = -1;

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, roteiro.getNome());
            stmt.setString(2, roteiro.getDuracao());
            stmt.setString(3, roteiro.getModalidade());
            stmt.setString(4, roteiro.getDescricao());
            
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    idGerado = rs.getInt(1);
                    roteiro.setId(idGerado);
                }
            }
        }
        return idGerado;
    }

    public List<Roteiro> buscarTodos() throws SQLException {
        List<Roteiro> lista = new ArrayList<>();
        String sql = "SELECT id, nome, duracao, modalidade, descricao FROM ROTEIRO";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(extrairRoteiro(rs, con));
            }
        }
        return lista;
    }

    public Roteiro buscarPorId(int id) throws SQLException {
        Roteiro roteiro = null;
        String sql = "SELECT id, nome, duracao, modalidade, descricao FROM ROTEIRO WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    roteiro = extrairRoteiro(rs, con);
                }
            }
        }
        return roteiro;
    }

    public void atualizar(Roteiro roteiro) throws SQLException {
        String sql = "UPDATE ROTEIRO SET nome = ?, duracao = ?, modalidade = ?, descricao = ? WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, roteiro.getNome());
            stmt.setString(2, roteiro.getDuracao());
            stmt.setString(3, roteiro.getModalidade());
            stmt.setString(4, roteiro.getDescricao());
            stmt.setInt(5, roteiro.getId());
            
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM ROTEIRO_PRECO WHERE id_roteiro = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM ROTEIRO WHERE id = ?")) {
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

    private Roteiro extrairRoteiro(ResultSet rs, Connection con) throws SQLException {
        Roteiro r = new Roteiro();
        r.setId(rs.getInt("id"));
        r.setNome(rs.getString("nome"));
        r.setDuracao(rs.getString("duracao"));
        r.setModalidade(rs.getString("modalidade"));
        r.setDescricao(rs.getString("descricao"));
        
        r.setHistoricoPrecos(buscarPrecos(r.getId(), con, r));
        
        return r;
    }

    private List<RoteiroPreco> buscarPrecos(int idRoteiro, Connection con, Roteiro roteiroPai) throws SQLException {
        List<RoteiroPreco> precos = new ArrayList<>();
        String sql = "SELECT id, data_cadastro, ativo, preco FROM ROTEIRO_PRECO WHERE id_roteiro = ? ORDER BY data_cadastro DESC";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idRoteiro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RoteiroPreco rp = new RoteiroPreco();
                    rp.setId(rs.getInt("id"));
                    rp.setAtivo(rs.getBoolean("ativo"));
                    rp.setPreco(rs.getInt("preco"));
                    
                    Timestamp data = rs.getTimestamp("data_cadastro");
                    if (data != null) {
                        rp.setDataCadastro(data.toLocalDateTime());
                    }
                    
                    rp.setRoteiro(roteiroPai); 
                    precos.add(rp);
                }
            }
        }
        return precos;
    }
}
