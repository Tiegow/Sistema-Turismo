package br.com.agencia.dao;

import br.com.agencia.config.ConnectionFactory;
import br.com.agencia.model.Motorista;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MotoristaDAO extends AbstractPessoaDAO {

    public void tornarMotorista(Motorista m) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                String sqlMot = "INSERT INTO MOTORISTA (id_colaborador, numero_cnh, validade) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = con.prepareStatement(sqlMot)) {
                    stmt.setInt(1, m.getId());
                    stmt.setInt(2, m.getNumeroCnh() != null ? m.getNumeroCnh() : 0);
                    stmt.setTimestamp(3, m.getValidade() != null ? Timestamp.valueOf(m.getValidade().atStartOfDay()) : null);
                    stmt.executeUpdate();
                }

                if (m.getCategoriasCnh() != null && !m.getCategoriasCnh().isEmpty()) {
                    String sqlCat = "INSERT INTO CNH_CATEGORIAS (id_motorista, cnh_categoria) VALUES (?, ?)";
                    try (PreparedStatement stmt = con.prepareStatement(sqlCat)) {
                        for (String categoria : m.getCategoriasCnh()) {
                            stmt.setInt(1, m.getId());
                            stmt.setString(2, categoria);
                            stmt.addBatch();
                        }
                        stmt.executeBatch();
                    }
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Motorista> buscarTodos() throws SQLException {
        List<Motorista> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.identificacao, p.nome, p.email, p.telefone, c.data_contratacao, c.pj, m.numero_cnh, m.validade " +
                     "FROM MOTORISTA m " +
                     "JOIN COLABORADOR c ON m.id_colaborador = c.id_pessoa " +
                     "JOIN PESSOA p ON c.id_pessoa = p.id";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(extrairMotorista(rs, con));
            }
        }
        return lista;
    }

    public Motorista buscarPorId(int id) throws SQLException {
        Motorista m = null;
        String sql = "SELECT p.id, p.identificacao, p.nome, p.email, p.telefone, c.data_contratacao, c.pj, m.numero_cnh, m.validade " +
                     "FROM MOTORISTA m " +
                     "JOIN COLABORADOR c ON m.id_colaborador = c.id_pessoa " +
                     "JOIN PESSOA p ON c.id_pessoa = p.id " +
                     "WHERE m.id_colaborador = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    m = extrairMotorista(rs, con);
                }
            }
        }
        return m;
    }

    public void atualizarDadosMotorista(Motorista m) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                String sqlMot = "UPDATE MOTORISTA SET numero_cnh = ?, validade = ? WHERE id_colaborador = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlMot)) {
                    stmt.setInt(1, m.getNumeroCnh() != null ? m.getNumeroCnh() : 0);
                    stmt.setTimestamp(2, m.getValidade() != null ? Timestamp.valueOf(m.getValidade().atStartOfDay()) : null);
                    stmt.setInt(3, m.getId());
                    stmt.executeUpdate();
                }

                String sqlDel = "DELETE FROM CNH_CATEGORIAS WHERE id_motorista = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlDel)) {
                    stmt.setInt(1, m.getId());
                    stmt.executeUpdate();
                }

                if (m.getCategoriasCnh() != null && !m.getCategoriasCnh().isEmpty()) {
                    String sqlCat = "INSERT INTO CNH_CATEGORIAS (id_motorista, cnh_categoria) VALUES (?, ?)";
                    try (PreparedStatement stmt = con.prepareStatement(sqlCat)) {
                        for (String categoria : m.getCategoriasCnh()) {
                            stmt.setInt(1, m.getId());
                            stmt.setString(2, categoria);
                            stmt.addBatch();
                        }
                        stmt.executeBatch();
                    }
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void removerPapelMotorista(int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM CNH_CATEGORIAS WHERE id_motorista = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM MOTORISTA WHERE id_colaborador = ?")) {
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

    private Motorista extrairMotorista(ResultSet rs, Connection con) throws SQLException {
        Motorista m = new Motorista();
        preencherDadosPessoa(m, rs);
        
        Timestamp dtContr = rs.getTimestamp("data_contratacao");
        if (dtContr != null) {
            m.setDataContratacao(dtContr.toLocalDateTime().toLocalDate());
        }
        m.setPj(rs.getBoolean("pj"));
        
        m.setNumeroCnh(rs.getInt("numero_cnh"));
        Timestamp dtVal = rs.getTimestamp("validade");
        if (dtVal != null) {
            m.setValidade(dtVal.toLocalDateTime().toLocalDate());
        }
        
        m.setCategoriasCnh(buscarCategorias(m.getId(), con));
        
        return m;
    }

    private List<String> buscarCategorias(int idMotorista, Connection con) throws SQLException {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT cnh_categoria FROM CNH_CATEGORIAS WHERE id_motorista = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idMotorista);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(rs.getString("cnh_categoria"));
                }
            }
        }
        return categorias;
    }
}
