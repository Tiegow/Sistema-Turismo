package br.com.agencia.dao;

import br.com.agencia.config.ConnectionFactory;
import br.com.agencia.model.Passeio;
import br.com.agencia.model.Roteiro;
import br.com.agencia.model.Colaborador;
import br.com.agencia.model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PasseioDAO {

    public int inserir(Passeio passeio) throws SQLException {
        String sqlPasseio = "INSERT INTO PASSEIO (preco, capacidade, data_hora, id_roteiro) VALUES (?, ?, ?, ?)";
        int idGerado = -1;

        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = con.prepareStatement(sqlPasseio, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, passeio.getPreco() != null ? passeio.getPreco() : 0);
                    stmt.setInt(2, passeio.getCapacidade() != null ? passeio.getCapacidade() : 0);
                    
                    LocalDateTime dt = passeio.getDataHora() != null ? passeio.getDataHora() : LocalDateTime.now();
                    stmt.setTimestamp(3, Timestamp.valueOf(dt));
                    
                    if (passeio.getRoteiro() != null && passeio.getRoteiro().getId() != null) {
                        stmt.setInt(4, passeio.getRoteiro().getId());
                    } else {
                        throw new SQLException("O passeio precisa estar atrelado a um Roteiro válido.");
                    }
                    
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            idGerado = rs.getInt(1);
                            passeio.setId(idGerado);
                        }
                    }
                }

                inserirAlocacoes(passeio, con);

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
        return idGerado;
    }

    public List<Passeio> buscarTodos() throws SQLException {
        List<Passeio> lista = new ArrayList<>();
        String sql = "SELECT id, preco, capacidade, data_hora, id_roteiro FROM PASSEIO";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(extrairPasseio(rs, con));
            }
        }
        return lista;
    }

    public Passeio buscarPorId(int id) throws SQLException {
        Passeio p = null;
        String sql = "SELECT id, preco, capacidade, data_hora, id_roteiro FROM PASSEIO WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    p = extrairPasseio(rs, con);
                }
            }
        }
        return p;
    }

    public void atualizar(Passeio passeio) throws SQLException {
        String sqlPasseio = "UPDATE PASSEIO SET preco = ?, capacidade = ?, data_hora = ?, id_roteiro = ? WHERE id = ?";

        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = con.prepareStatement(sqlPasseio)) {
                    stmt.setInt(1, passeio.getPreco() != null ? passeio.getPreco() : 0);
                    stmt.setInt(2, passeio.getCapacidade() != null ? passeio.getCapacidade() : 0);
                    
                    LocalDateTime dt = passeio.getDataHora() != null ? passeio.getDataHora() : LocalDateTime.now();
                    stmt.setTimestamp(3, Timestamp.valueOf(dt));
                    
                    stmt.setInt(4, passeio.getRoteiro().getId());
                    stmt.setInt(5, passeio.getId());
                    
                    stmt.executeUpdate();
                }

                limparAlocacoes(passeio.getId(), con);
                inserirAlocacoes(passeio, con);

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
                limparAlocacoes(id, con);

                String sql = "DELETE FROM PASSEIO WHERE id = ?";
                try (PreparedStatement stmt = con.prepareStatement(sql)) {
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

    private void inserirAlocacoes(Passeio passeio, Connection con) throws SQLException {
        if (passeio.getColaboradoresAlocados() != null && !passeio.getColaboradoresAlocados().isEmpty()) {
            String sqlCol = "INSERT INTO COLABORADOR_ALOCADO (id_colaborador, id_passeio) VALUES (?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sqlCol)) {
                for (Colaborador c : passeio.getColaboradoresAlocados()) {
                    stmt.setInt(1, c.getId());
                    stmt.setInt(2, passeio.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }

        if (passeio.getVeiculosAlocados() != null && !passeio.getVeiculosAlocados().isEmpty()) {
            String sqlVei = "INSERT INTO VEICULO_ALOCADO (id_passeio, id_veiculo) VALUES (?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(sqlVei)) {
                for (Veiculo v : passeio.getVeiculosAlocados()) {
                    stmt.setInt(1, passeio.getId());
                    stmt.setInt(2, v.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    private void limparAlocacoes(int idPasseio, Connection con) throws SQLException {
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM COLABORADOR_ALOCADO WHERE id_passeio = ?")) {
            stmt.setInt(1, idPasseio);
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM VEICULO_ALOCADO WHERE id_passeio = ?")) {
            stmt.setInt(1, idPasseio);
            stmt.executeUpdate();
        }
    }

    private Passeio extrairPasseio(ResultSet rs, Connection con) throws SQLException {
        Passeio p = new Passeio();
        p.setId(rs.getInt("id"));
        p.setPreco(rs.getInt("preco"));
        p.setCapacidade(rs.getInt("capacidade"));
        
        Timestamp dt = rs.getTimestamp("data_hora");
        if (dt != null) p.setDataHora(dt.toLocalDateTime());
        
        Roteiro r = new Roteiro();
        r.setId(rs.getInt("id_roteiro"));
        p.setRoteiro(r); 

        p.setVagasDisp(calcularVagasDisponiveis(p, con));

        p.setVeiculosAlocados(buscarVeiculosAlocados(p.getId(), con));
        p.setColaboradoresAlocados(buscarColaboradoresAlocados(p.getId(), con));

        return p;
    }

    private int calcularVagasDisponiveis(Passeio p, Connection con) throws SQLException {
        int vagasOcupadas = 0;
        String sql = "SELECT SUM(qtd_vagas) as total FROM RESERVA WHERE id_passeio = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, p.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    vagasOcupadas = rs.getInt("total");
                }
            }
        }
        return p.getCapacidade() - vagasOcupadas;
    }

    private List<Veiculo> buscarVeiculosAlocados(int idPasseio, Connection con) throws SQLException {
        List<Veiculo> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.placa FROM VEICULO_ALOCADO va JOIN VEICULO v ON va.id_veiculo = v.id WHERE va.id_passeio = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idPasseio);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Veiculo v = new Veiculo();
                    v.setId(rs.getInt("id"));
                    v.setPlaca(rs.getString("placa"));
                    lista.add(v);
                }
            }
        }
        return lista;
    }

    private List<Colaborador> buscarColaboradoresAlocados(int idPasseio, Connection con) throws SQLException {
        List<Colaborador> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.nome FROM COLABORADOR_ALOCADO ca JOIN PESSOA p ON ca.id_colaborador = p.id WHERE ca.id_passeio = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idPasseio);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Colaborador c = new Colaborador();
                    c.setId(rs.getInt("id"));
                    c.setNome(rs.getString("nome"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }
}
