package br.com.agencia.dao;

import br.com.agencia.config.ConnectionFactory;
import br.com.agencia.model.Colaborador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorDAO extends AbstractPessoaDAO {

    public void inserir(Colaborador colaborador) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                int idGerado = inserirPessoa(colaborador, con);

                String sqlColaborador = "INSERT INTO COLABORADOR (id_pessoa, data_contratacao, pj) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = con.prepareStatement(sqlColaborador)) {
                    stmt.setInt(1, idGerado);
                    stmt.setTimestamp(2, colaborador.getDataContratacao() != null ? Timestamp.valueOf(colaborador.getDataContratacao().atStartOfDay()) : null);
                    stmt.setBoolean(3, colaborador.getPj() != null ? colaborador.getPj() : false);
                    stmt.executeUpdate();
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Colaborador> buscarTodos() throws SQLException {
        List<Colaborador> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.identificacao, p.nome, p.email, p.telefone, c.data_contratacao, c.pj " +
                     "FROM COLABORADOR c " +
                     "JOIN PESSOA p ON c.id_pessoa = p.id";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(extrairColaborador(rs));
            }
        }
        return lista;
    }

    public Colaborador buscarPorId(int id) throws SQLException {
        Colaborador c = null;
        String sql = "SELECT p.id, p.identificacao, p.nome, p.email, p.telefone, c.data_contratacao, c.pj " +
                     "FROM COLABORADOR c " +
                     "JOIN PESSOA p ON c.id_pessoa = p.id " +
                     "WHERE p.id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    c = extrairColaborador(rs);
                }
            }
        }
        return c;
    }

    public void atualizar(Colaborador colaborador) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                atualizarPessoa(colaborador, con);

                String sqlColaborador = "UPDATE COLABORADOR SET data_contratacao = ?, pj = ? WHERE id_pessoa = ?";
                try (PreparedStatement stmt = con.prepareStatement(sqlColaborador)) {
                    stmt.setTimestamp(1, colaborador.getDataContratacao() != null ? Timestamp.valueOf(colaborador.getDataContratacao().atStartOfDay()) : null);
                    stmt.setBoolean(2, colaborador.getPj() != null ? colaborador.getPj() : false);
                    stmt.setInt(3, colaborador.getId());
                    stmt.executeUpdate();
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void deletar(int id) throws SQLException {
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

                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM CNH_CATEGORIAS WHERE id_motorista = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM MOTORISTA WHERE id_colaborador = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM COLABORADOR_ALOCADO WHERE id_colaborador = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                String sql = "DELETE FROM COLABORADOR WHERE id_pessoa = ?";
                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                deletarPessoa(id, con);

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }


    private Colaborador extrairColaborador(ResultSet rs) throws SQLException {
        Colaborador c = new Colaborador();
        preencherDadosPessoa(c, rs);
        Timestamp dataContratacao = rs.getTimestamp("data_contratacao");
        if (dataContratacao != null) {
            c.setDataContratacao(dataContratacao.toLocalDateTime().toLocalDate());
        }
        c.setPj(rs.getBoolean("pj"));
        return c;
    }
}
