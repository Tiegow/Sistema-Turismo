package br.com.agencia.service;

import br.com.agencia.dao.ColaboradorDAO;
import br.com.agencia.dao.PasseioDAO;
import br.com.agencia.dao.RoteiroDAO;
import br.com.agencia.dao.VeiculoDAO;
import br.com.agencia.model.Colaborador;
import br.com.agencia.model.Passeio;
import br.com.agencia.model.Roteiro;
import br.com.agencia.model.Veiculo;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class PasseioService {

    private final PasseioDAO passeioDAO = new PasseioDAO();
    private final RoteiroDAO roteiroDAO = new RoteiroDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

    public List<Passeio> listarTodos() {
        try {
            return passeioDAO.buscarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar os passeios do banco.", e);
        }
    }

    public Passeio buscarPorId(int id) {
        try {
            return passeioDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao localizar passeio.", e);
        }
    }

    public void salvar(Passeio passeio) {
        try {
            if (passeio.getId() == null || passeio.getId() == 0) {
                passeioDAO.inserir(passeio);
            } else {
                passeioDAO.atualizar(passeio);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao agendar passeio. Verifique os dados inseridos.", e);
        }
    }

    public void deletar(int id) {
        try {
            passeioDAO.deletar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Não é possível cancelar um passeio que possui Reservas atreladas a ele.", e);
        }
    }

    // --- MÉTODOS DE APOIO PARA OS FORMULÁRIOS (DROPDOWNS) ---

    public List<Roteiro> listarRoteirosDisponiveis() {
        try {
            return roteiroDAO.buscarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar roteiros.", e);
        }
    }

    public List<Veiculo> listarVeiculosDisponiveis() {
        try {
            return veiculoDAO.buscarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar veículos.", e);
        }
    }

    public List<Colaborador> listarColaboradoresDisponiveis() {
        try {
            return colaboradorDAO.buscarTodosComPapeis();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar colaboradores com seus papéis.", e);
        }
    }
}
