package br.com.agencia.dao;

import br.com.agencia.model.Pessoa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractPessoaDAO {

    protected int inserirPessoa(Pessoa p, Connection con) throws SQLException {
        String sql = "INSERT INTO PESSOA (identificacao, nome, email, telefone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getIdentificacao());
            stmt.setString(2, p.getNome());
            stmt.setString(3, p.getEmail());
            stmt.setString(4, p.getTelefone());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getInt(1)); 
                    return p.getId();
                }
            }
        }
        throw new SQLException("Falha ao gerar ID para Pessoa.");
    }

    protected void preencherDadosPessoa(Pessoa p, ResultSet rs) throws SQLException {
        p.setId(rs.getInt("id"));
        p.setIdentificacao(rs.getString("identificacao"));
        p.setNome(rs.getString("nome"));
        p.setEmail(rs.getString("email"));
        p.setTelefone(rs.getString("telefone"));
    }

    protected void atualizarPessoa(Pessoa p, Connection con) throws SQLException {
        String sql = "UPDATE PESSOA SET identificacao = ?, nome = ?, email = ?, telefone = ? WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, p.getIdentificacao());
            stmt.setString(2, p.getNome());
            stmt.setString(3, p.getEmail());
            stmt.setString(4, p.getTelefone());
            stmt.setInt(5, p.getId());
            stmt.executeUpdate();
        }
    }
    
    protected void deletarPessoa(int id, Connection con) throws SQLException {
        String sql = "DELETE FROM PESSOA WHERE id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
