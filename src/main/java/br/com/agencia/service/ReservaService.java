package br.com.agencia.service;

import br.com.agencia.dao.PasseioDAO;
import br.com.agencia.dao.ReservaDAO;
import br.com.agencia.dao.RoteiroDAO;
import br.com.agencia.dao.TuristaDAO;
import br.com.agencia.model.Passeio;
import br.com.agencia.model.Reserva;
import br.com.agencia.model.Turista;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final PasseioDAO passeioDAO = new PasseioDAO();
    private final TuristaDAO turistaDAO = new TuristaDAO();
    private final RoteiroDAO roteiroDAO = new RoteiroDAO();

    public List<Reserva> listarTodas() {
        try {
            List<Reserva> reservas = reservaDAO.buscarTodos();
            for (Reserva r : reservas) {
                r.setPasseio(passeioDAO.buscarPorId(r.getPasseio().getId()));
                r.setTurista(turistaDAO.buscarPorId(r.getTurista().getId()));
            }
            return reservas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reservas do banco.", e);
        }
    }

    public Reserva buscarPorId(int idPasseio, int idTurista) {
        try {
            List<Reserva> reservas = reservaDAO.buscarPorPasseio(idPasseio);
            for (Reserva r : reservas) {
                if (r.getTurista().getId().equals(idTurista)) {
                    r.setPasseio(passeioDAO.buscarPorId(r.getPasseio().getId()));
                    r.setTurista(turistaDAO.buscarPorId(r.getTurista().getId()));
                    return r;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reserva.", e);
        }
    }

    public void salvar(Reserva reserva) {
        try {
            Passeio passeio = passeioDAO.buscarPorId(reserva.getPasseio().getId());
            if (passeio == null) throw new IllegalArgumentException("Passeio selecionado não é válido.");

            // Checagem de Overbooking
            int vagasExistentes = 0;
            Reserva existente = buscarPorId(passeio.getId(), reserva.getTurista().getId());
            if (existente != null) {
                vagasExistentes = existente.getQtdVagas();
            }

            int diferencaDeVagas = reserva.getQtdVagas() - vagasExistentes;
            if (diferencaDeVagas > passeio.getVagasDisp()) {
                throw new IllegalArgumentException("OVERBOOKING! Tentativa de alocar " + diferencaDeVagas + " novas vagas, porém o passeio possui apenas " + passeio.getVagasDisp() + " vagas disponíveis.");
            }

            reserva.setValorTotal(passeio.getPreco() * reserva.getQtdVagas());

            if (existente == null) {
                reservaDAO.inserir(reserva);
            } else {
                reservaDAO.atualizar(reserva);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao processar a venda de reserva.", e);
        }
    }

    public void deletar(int idPasseio, int idTurista) {
        try {
            reservaDAO.deletar(idPasseio, idTurista);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir reserva.", e);
        }
    }
    
    public List<Passeio> listarPasseiosDisponiveis() {
        try {
            List<Passeio> passeios = passeioDAO.buscarTodos();
            for (Passeio p : passeios) {
                p.setRoteiro(roteiroDAO.buscarPorId(p.getRoteiro().getId()));
            }
            return passeios;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar passeios.", e);
        }
    }

    public List<Turista> listarTuristas() {
        try {
            return turistaDAO.buscarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar turistas.", e);
        }
    }
}
