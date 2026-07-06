package br.com.agencia.service;

import br.com.agencia.dao.ColaboradorDAO;
import br.com.agencia.dao.GuiaDAO;
import br.com.agencia.dao.MotoristaDAO;
import br.com.agencia.model.Colaborador;
import br.com.agencia.model.Guia;
import br.com.agencia.model.Motorista;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ColaboradorService {
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();
    private final GuiaDAO guiaDAO = new GuiaDAO();

    public List<Colaborador> listarTodos() throws SQLException {
        return colaboradorDAO.buscarTodos();
    }
    public Motorista buscarMotorista(int id) throws SQLException {
        return motoristaDAO.buscarPorId(id);
    }
    public Guia buscarGuia(int id) throws SQLException {
        return guiaDAO.buscarPorId(id);
    }
    public Colaborador buscarPorId(int id) throws SQLException {
        return colaboradorDAO.buscarPorId(id);
    }

    public void salvar(Colaborador colaborador, Motorista motorista, Guia guia, boolean ehMotorista, boolean ehGuia) {
        limparListasVazias(motorista, guia);
        try {
            if (colaborador.getId() == null || colaborador.getId() == 0) {
                colaboradorDAO.inserir(colaborador);
                int id = colaborador.getId();
                motorista.setId(id);
                guia.setId(id);
                try {
                    if (ehMotorista) motoristaDAO.tornarMotorista(motorista);
                    if (ehGuia) guiaDAO.tornarGuia(guia);
                } catch (SQLException e) {
                    colaboradorDAO.deletar(id);
                    throw e;
                }
            } else {
                colaboradorDAO.atualizar(colaborador);
                int id = colaborador.getId();
                motorista.setId(id);
                guia.setId(id);
                
                boolean jaMotorista = motoristaDAO.buscarPorId(id) != null;
                if (ehMotorista) {
                    if (jaMotorista) motoristaDAO.atualizarDadosMotorista(motorista);
                    else motoristaDAO.tornarMotorista(motorista);
                } else if (jaMotorista) {
                    motoristaDAO.removerPapelMotorista(id);
                }
                
                boolean jaGuia = guiaDAO.buscarPorId(id) != null;
                if (ehGuia) {
                    if (jaGuia) guiaDAO.atualizarIdiomas(guia);
                    else guiaDAO.tornarGuia(guia);
                } else if (jaGuia) {
                    guiaDAO.removerPapelGuia(id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar dados operacionais. Verifique as restrições.", e);
        }
    }

    public void deletar(int id) {
        try {
            colaboradorDAO.deletar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Não é possível apagar um colaborador que já está alocado em Passeios.", e);
        }
    }

    private void limparListasVazias(Motorista motorista, Guia guia) {
        if (motorista != null && motorista.getCategoriasCnh() != null) {
            motorista.getCategoriasCnh().removeIf(c -> c == null || c.isBlank());
        }
        if (guia != null && guia.getIdiomas() != null) {
            guia.getIdiomas().removeIf(i -> i == null || i.isBlank());
        }
    }
}
