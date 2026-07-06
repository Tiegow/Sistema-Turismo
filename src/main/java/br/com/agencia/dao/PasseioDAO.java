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
                    stmt.setDouble(1, passeio.getPreco() != null ? passeio.getPreco() : 0.0);
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
        String sql = "SELECT p.id, p.preco, p.capacidade, p.data_hora, p.id_roteiro, r.nome as roteiro_nome FROM PASSEIO p JOIN ROTEIRO r ON p.id_roteiro = r.id";

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
        String sql = "SELECT p.id, p.preco, p.capacidade, p.data_hora, p.id_roteiro, r.nome as roteiro_nome FROM PASSEIO p JOIN ROTEIRO r ON p.id_roteiro = r.id WHERE p.id = ?";

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
                    stmt.setDouble(1, passeio.getPreco() != null ? passeio.getPreco() : 0.0);
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
        p.setPreco(rs.getDouble("preco"));
        p.setCapacidade(rs.getInt("capacidade"));
        
        Timestamp dt = rs.getTimestamp("data_hora");
        if (dt != null) p.setDataHora(dt.toLocalDateTime());
        
        Roteiro r = new Roteiro();
        r.setId(rs.getInt("id_roteiro"));
        r.setNome(rs.getString("roteiro_nome"));
        p.setRoteiro(r); 

        p.setVagasDisp(calcularVagasDisponiveis(p, con));

        p.setVeiculosAlocados(buscarVeiculosAlocados(p.getId(), con));
        p.setColaboradoresAlocados(buscarColaboradoresAlocados(p.getId(), con));
        
        p.setAlertaMotorista(verificarAlertaMotorista(p.getId(), p.getVeiculosAlocados().size(), con));
        p.setAlertaGuia(verificarAlertaGuia(p.getId(), con));

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

    private boolean verificarAlertaMotorista(int idPasseio, int qtdVeiculos, Connection con) throws SQLException {
        if (qtdVeiculos == 0) return false; 
        int qtdMotoristas = 0;
        String sql = "SELECT count(*) as total FROM COLABORADOR_ALOCADO ca " +
                     "JOIN MOTORISTA m ON ca.id_colaborador = m.id_colaborador " +
                     "WHERE ca.id_passeio = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idPasseio);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    qtdMotoristas = rs.getInt("total");
                }
            }
        }
        return qtdMotoristas < qtdVeiculos;
    }

    private boolean verificarAlertaGuia(int idPasseio, Connection con) throws SQLException {
        int qtdGuias = 0;
        String sql = "SELECT count(*) as total FROM COLABORADOR_ALOCADO ca " +
                     "JOIN GUIA g ON ca.id_colaborador = g.id_colaborador " +
                     "WHERE ca.id_passeio = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idPasseio);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    qtdGuias = rs.getInt("total");
                }
            }
        }
        return qtdGuias == 0;
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

    public boolean verificarConflitoColaborador(int idColaborador, LocalDateTime inicio, LocalDateTime fim, int idPasseioIgnorado) throws SQLException {
        String sql = "SELECT count(*) as total FROM PASSEIO p " +
                     "JOIN ROTEIRO r ON p.id_roteiro = r.id " +
                     "JOIN COLABORADOR_ALOCADO ca ON ca.id_passeio = p.id " +
                     "WHERE ca.id_colaborador = ? AND p.id != ? " +
                     "  AND p.data_hora < ? " +
                     "  AND DATE_ADD(p.data_hora, INTERVAL COALESCE(r.duracao, 0) MINUTE) > ?";
        
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idColaborador);
            stmt.setInt(2, idPasseioIgnorado);
            stmt.setTimestamp(3, Timestamp.valueOf(fim));
            stmt.setTimestamp(4, Timestamp.valueOf(inicio));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        }
        return false;
    }

    public boolean verificarConflitoVeiculo(int idVeiculo, LocalDateTime inicio, LocalDateTime fim, int idPasseioIgnorado) throws SQLException {
        String sql = "SELECT count(*) as total FROM PASSEIO p " +
                     "JOIN ROTEIRO r ON p.id_roteiro = r.id " +
                     "JOIN VEICULO_ALOCADO va ON va.id_passeio = p.id " +
                     "WHERE va.id_veiculo = ? AND p.id != ? " +
                     "  AND p.data_hora < ? " +
                     "  AND DATE_ADD(p.data_hora, INTERVAL COALESCE(r.duracao, 0) MINUTE) > ?";
        
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idVeiculo);
            stmt.setInt(2, idPasseioIgnorado);
            stmt.setTimestamp(3, Timestamp.valueOf(fim));
            stmt.setTimestamp(4, Timestamp.valueOf(inicio));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        }
        return false;
    }
}
