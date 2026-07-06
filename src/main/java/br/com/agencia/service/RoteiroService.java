package br.com.agencia.service;

import br.com.agencia.dao.RoteiroDAO;
import br.com.agencia.dao.RoteiroPrecoDAO;
import br.com.agencia.model.Roteiro;
import br.com.agencia.model.RoteiroPreco;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class RoteiroService {

    private final RoteiroDAO roteiroDAO;
    private final RoteiroPrecoDAO precoDAO;

    public RoteiroService() {
        this.roteiroDAO = new RoteiroDAO();
        this.precoDAO = new RoteiroPrecoDAO();
    }

    public List<Roteiro> listarTodos() {
        try {
            return roteiroDAO.buscarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar roteiros", e);
        }
    }

    public Roteiro buscarPorId(int id) {
        try {
            return roteiroDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar roteiro", e);
        }
    }
    
    public RoteiroPreco buscarPrecoAtivo(int idRoteiro) {
        try {
            return precoDAO.buscarPrecoAtivo(idRoteiro);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar preço ativo", e);
        }
    }

    public void salvar(Roteiro roteiro, Double novoPreco) {
        try {
            if (roteiro.getId() == null || roteiro.getId() == 0) {
                // Inserir 
                roteiroDAO.inserir(roteiro);
                int idGerado = roteiro.getId();
                if (novoPreco != null) {
                    RoteiroPreco rp = new RoteiroPreco();
                    rp.setPreco(novoPreco);
                    rp.setAtivo(true);
                    precoDAO.inserir(rp, idGerado);
                }
            } else {
                // Atualizar 
                roteiroDAO.atualizar(roteiro);
                if (novoPreco != null) {
                    // Atualizar preço apenas se o valor mudou
                    RoteiroPreco ativo = precoDAO.buscarPrecoAtivo(roteiro.getId());
                    if (ativo == null || !ativo.getPreco().equals(novoPreco)) {
                        precoDAO.inativarPrecoAntigos(roteiro.getId());
                        RoteiroPreco rp = new RoteiroPreco();
                        rp.setPreco(novoPreco);
                        rp.setAtivo(true);
                        precoDAO.inserir(rp, roteiro.getId());
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar roteiro e preços", e);
        }
    }

    public void deletar(int id) {
        try {
            roteiroDAO.deletar(id);
        } catch (SQLException e) {
            throw new RuntimeException("Não é possível apagar um roteiro que já possui Passeios vinculados no histórico.", e);
        }
    }
}
