package br.com.agencia.dao;

import br.com.agencia.config.ConnectionFactory;
import br.com.agencia.model.Turista;
import br.com.agencia.model.Acompanhante;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TuristaDAO extends AbstractPessoaDAO {

    public void inserir(Turista turista) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                int idGerado = inserirPessoa(turista, con);

                String sqlTurista = "INSERT INTO TURISTA (id_pessoa, forma_pagamento) VALUES (?, ?)";
                try (PreparedStatement stmtTurista = con.prepareStatement(sqlTurista)) {
                    stmtTurista.setInt(1, idGerado); 
                    stmtTurista.setString(2, turista.getFormaPagamento());
                    stmtTurista.executeUpdate();
                }
                
                if (turista.getAcompanhantes() != null && !turista.getAcompanhantes().isEmpty()) {
                    String sqlAcomp = "INSERT INTO ACOMPANHANTE (nome, telefone, id_turista) VALUES (?, ?, ?)";
                    try (PreparedStatement stmtAcomp = con.prepareStatement(sqlAcomp)) {
                        for (Acompanhante acomp : turista.getAcompanhantes()) {
                            stmtAcomp.setString(1, acomp.getNome());
                            stmtAcomp.setString(2, acomp.getTelefone());
                            stmtAcomp.setInt(3, idGerado);
                            stmtAcomp.addBatch();
                        }
                        stmtAcomp.executeBatch();
                    }
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Turista> buscarTodos() throws SQLException {
        List<Turista> turistas = new ArrayList<>();
        
        String sql = "SELECT p.id, p.identificacao, p.nome, p.email, p.telefone, t.forma_pagamento " +
                     "FROM TURISTA t " +
                     "JOIN PESSOA p ON t.id_pessoa = p.id";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                turistas.add(extrairTurista(rs, con));
            }
        }
        return turistas;
    }

    public Turista buscarPorId(int id) throws SQLException {
        Turista t = null;
        String sql = "SELECT p.id, p.identificacao, p.nome, p.email, p.telefone, t.forma_pagamento " +
                     "FROM TURISTA t " +
                     "JOIN PESSOA p ON t.id_pessoa = p.id " +
                     "WHERE p.id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
             
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    t = extrairTurista(rs, con);
                }
            }
        }
        return t;
    }

    public void atualizar(Turista turista) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                atualizarPessoa(turista, con);

                String sqlTurista = "UPDATE TURISTA SET forma_pagamento = ? WHERE id_pessoa = ?";
                try (PreparedStatement stmtTurista = con.prepareStatement(sqlTurista)) {
                    stmtTurista.setString(1, turista.getFormaPagamento());
                    stmtTurista.setInt(2, turista.getId());
                    stmtTurista.executeUpdate();
                }

                String sqlDeleteAcomp = "DELETE FROM ACOMPANHANTE WHERE id_turista = ?";
                try (PreparedStatement stmtDel = con.prepareStatement(sqlDeleteAcomp)) {
                    stmtDel.setInt(1, turista.getId());
                    stmtDel.executeUpdate();
                }

                if (turista.getAcompanhantes() != null && !turista.getAcompanhantes().isEmpty()) {
                    String sqlAcomp = "INSERT INTO ACOMPANHANTE (nome, telefone, id_turista) VALUES (?, ?, ?)";
                    try (PreparedStatement stmtAcomp = con.prepareStatement(sqlAcomp)) {
                        for (Acompanhante acomp : turista.getAcompanhantes()) {
                            stmtAcomp.setString(1, acomp.getNome());
                            stmtAcomp.setString(2, acomp.getTelefone());
                            stmtAcomp.setInt(3, turista.getId());
                            stmtAcomp.addBatch();
                        }
                        stmtAcomp.executeBatch();
                    }
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
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM ACOMPANHANTE WHERE id_turista = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }
                
                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM RESERVA WHERE id_turista = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                }

                try (PreparedStatement stmt = con.prepareStatement("DELETE FROM TURISTA WHERE id_pessoa = ?")) {
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

    private Turista extrairTurista(ResultSet rs, Connection con) throws SQLException {
        Turista t = new Turista();
        preencherDadosPessoa(t, rs);
        t.setFormaPagamento(rs.getString("forma_pagamento"));
        t.setAcompanhantes(buscarAcompanhantes(con, t.getId()));
        return t;
    }

    private List<Acompanhante> buscarAcompanhantes(Connection con, int idTurista) throws SQLException {
        List<Acompanhante> lista = new ArrayList<>();
        String sql = "SELECT id, nome, telefone FROM ACOMPANHANTE WHERE id_turista = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idTurista);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Acompanhante a = new Acompanhante();
                    a.setId(rs.getInt("id"));
                    a.setNome(rs.getString("nome"));
                    a.setTelefone(rs.getString("telefone"));
                    lista.add(a);
                }
            }
        }
        return lista;
    }
}
