package br.com.agencia.service;

import br.com.agencia.dao.TuristaDAO;
import br.com.agencia.model.Turista;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.List;

@Service
public class TuristaService {
    private final TuristaDAO turistaDAO = new TuristaDAO();

    public List<Turista> listarTodos() {
        try { return turistaDAO.buscarTodos(); }
        catch (SQLException e) { throw new RuntimeException("Erro ao buscar turistas", e); }
    }

    public Turista buscarPorId(int id) {
        try { return turistaDAO.buscarPorId(id); }
        catch (SQLException e) { throw new RuntimeException("Erro ao buscar turista", e); }
    }

    public void salvar(Turista turista) {
        if (turista.getAcompanhantes() != null) {
            turista.getAcompanhantes().removeIf(a -> a == null || a.getNome() == null || a.getNome().isBlank());
        }
        try {
            if (turista.getId() == null || turista.getId() == 0) {
                turistaDAO.inserir(turista);
            } else {
                turistaDAO.atualizar(turista);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar turista. Verifique se a identificação já não está cadastrada.", e);
        }
    }

    public void deletar(int id) {
        try { turistaDAO.deletar(id); }
        catch (SQLException e) { throw new RuntimeException("Não é possível excluir um turista que já possui histórico no sistema.", e); }
    }
}
