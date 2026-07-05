package br.com.agencia.controller;

import br.com.agencia.dao.RoteiroDAO;
import br.com.agencia.dao.RoteiroPrecoDAO;
import br.com.agencia.model.Roteiro;
import br.com.agencia.model.RoteiroPreco;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RoteiroController {

    private final RoteiroDAO roteiroDAO = new RoteiroDAO();
    private final RoteiroPrecoDAO roteiroPrecoDAO = new RoteiroPrecoDAO();

    @GetMapping("/roteiros")
    public String listar(Model model) {
        try {
            List<Roteiro> roteiros = roteiroDAO.buscarTodos();
            Map<Integer, RoteiroPreco> precosAtivos = new HashMap<>();
            for (Roteiro r : roteiros) {
                RoteiroPreco ativo = roteiroPrecoDAO.buscarPrecoAtivo(r.getId());
                if (ativo != null) {
                    precosAtivos.put(r.getId(), ativo);
                }
            }
            model.addAttribute("roteiros", roteiros);
            model.addAttribute("precosAtivos", precosAtivos);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao carregar roteiros do banco de dados!");
        }
        return "roteiros";
    }

    @GetMapping("/roteiros/novo")
    public String formNovo(Model model) {
        model.addAttribute("roteiro", new Roteiro());
        return "roteiro-form";
    }

    @PostMapping("/roteiros")
    public String salvar(@ModelAttribute Roteiro roteiro,
                         @RequestParam(required = false) Integer preco,
                         Model model) {
        try {
            roteiroDAO.inserir(roteiro);
            int id = roteiro.getId();

            if (preco != null && preco > 0) {
                RoteiroPreco roteiroPreco = new RoteiroPreco();
                roteiroPreco.setPreco(preco);
                roteiroPreco.setAtivo(true);
                roteiroPrecoDAO.inserir(roteiroPreco, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao salvar roteiro!");
            model.addAttribute("roteiro", roteiro);
            return "roteiro-form";
        }
        return "redirect:/roteiros";
    }

    @GetMapping("/roteiros/{id}/editar")
    public String formEditar(@PathVariable int id, Model model) {
        try {
            Roteiro roteiro = roteiroDAO.buscarPorId(id);
            if (roteiro == null) {
                return "redirect:/roteiros";
            }
            RoteiroPreco precoAtual = roteiroPrecoDAO.buscarPrecoAtivo(id);
            model.addAttribute("roteiro", roteiro);
            model.addAttribute("precoAtual", precoAtual);
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/roteiros";
        }
        return "roteiro-form";
    }

    @PostMapping("/roteiros/{id}")
    public String atualizar(@PathVariable int id,
                            @ModelAttribute Roteiro roteiro,
                            @RequestParam(required = false) Integer novoPreco,
                            Model model) {
        roteiro.setId(id);
        try {
            roteiroDAO.atualizar(roteiro);

            if (novoPreco != null && novoPreco > 0) {
                roteiroPrecoDAO.inativarPrecoAntigos(id);
                RoteiroPreco roteiroPreco = new RoteiroPreco();
                roteiroPreco.setPreco(novoPreco);
                roteiroPreco.setAtivo(true);
                roteiroPrecoDAO.inserir(roteiroPreco, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao atualizar roteiro!");
            model.addAttribute("roteiro", roteiro);
            return "roteiro-form";
        }
        return "redirect:/roteiros";
    }

    @PostMapping("/roteiros/{id}/deletar")
    public String deletar(@PathVariable int id) {
        try {
            roteiroDAO.deletar(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/roteiros";
    }
}
