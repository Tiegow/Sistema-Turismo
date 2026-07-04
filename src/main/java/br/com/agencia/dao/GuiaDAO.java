package br.com.agencia.dao;

import br.com.agencia.config.ConnectionFactory;
import br.com.agencia.model.Guia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GuiaDAO extends AbstractPessoaDAO {

    /**
     * Assume que o funcionário já está cadastrado na tabela COLABORADOR.
     * Atribui a ele o papel de GUIA e insere seus idiomas.
     */
    public void tornarGuia(Guia guia) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                String sqlGuia = "INSERT INTO GUIA (id_colaborador) VALUES (?)";
                try (PreparedStatement stmt = con.prepareStatement(sqlGuia)) {
                    stmt.setInt(1, guia.getId());
                    stmt.executeUpdate();
                }

                if (guia.getIdiomas() != null && !guia.getIdiomas().isEmpty()) {
                    String sqlIdiomas = "INSERT INTO IDIOMAS (id_guia, idioma) VALUES (?, ?)";
                    try (PreparedStatement stmt = con.prepareStatement(sqlIdiomas)) {
                        for (String idioma : guia.getIdiomas()) {
                            stmt.setInt(1, guia.getId());
                            stmt.setString(2, idioma);
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

    public List<Guia> buscarTodos() throws SQLException {
        List<Guia> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.identificacao, p.nome, p.email, p.telefone, c.data_contratacao, c.pj " +
                     "FROM GUIA g " +
                     "JOIN COLABORADOR c ON g.id_colaborador = c.id_pessoa " +
                     "JOIN PESSOA p ON c.id_pessoa = p.id";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(extrairGuia(rs, con));
            }
        }
        return lista;
    }

    public Guia buscarPorId(int id) throws SQLException {
        Guia g = null;
        String sql = "SELECT p.id, p.identificacao, p.nome, p.email, p.telefone, c.data_contratacao, c.pj " +
                     "FROM GUIA g " +
                     "JOIN COLABORADOR c ON g.id_colaborador = c.id_pessoa " +
                     "JOIN PESSOA p ON c.id_pessoa = p.id " +
                     "WHERE g.id_colaborador = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    g = extrairGuia(rs, con);
                }
            }
        }
        return g;
    }

    public void atualizarIdiomas(Guia guia) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                String sqlDel = "DELETE FROM IDIOMAS WHERE id_guia = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlDel)) {
                    stmt.setInt(1, guia.getId());
                    stmt.executeUpdate();
                }

                if (guia.getIdiomas() != null && !guia.getIdiomas().isEmpty()) {
                    String sqlIdiomas = "INSERT INTO IDIOMAS (id_guia, idioma) VALUES (?, ?)";
                    try (PreparedStatement stmt = con.prepareStatement(sqlIdiomas)) {
                        for (String idioma : guia.getIdiomas()) {
                            stmt.setInt(1, guia.getId());
                            stmt.setString(2, idioma);
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

    public void removerPapelGuia(int id) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM IDIOMAS WHERE id_guia = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM GUIA WHERE id_colaborador = ?")) {
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

    private Guia extrairGuia(ResultSet rs, Connection con) throws SQLException {
        Guia g = new Guia();
        preencherDadosPessoa(g, rs); 
        
        Timestamp dt = rs.getTimestamp("data_contratacao");
        if (dt != null) {
            g.setDataContratacao(dt.toLocalDateTime());
        }
        g.setPj(rs.getBoolean("pj"));
        
        g.setIdiomas(buscarIdiomas(g.getId(), con));
        
        return g;
    }

    private List<String> buscarIdiomas(int idGuia, Connection con) throws SQLException {
        List<String> idiomas = new ArrayList<>();
        String sql = "SELECT idioma FROM IDIOMAS WHERE id_guia = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idGuia);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    idiomas.add(rs.getString("idioma"));
                }
            }
        }
        return idiomas;
    }
}
