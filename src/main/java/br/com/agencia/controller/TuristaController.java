package br.com.agencia.controller;

import br.com.agencia.dao.TuristaDAO;
import br.com.agencia.model.Turista;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.SQLException;
import java.util.List;

@Controller
public class TuristaController {

    private final TuristaDAO turistaDAO = new TuristaDAO();

    @GetMapping("/turistas")
    public String listarTuristas(Model model) {
        try {
            List<Turista> turistas = turistaDAO.buscarTodos();
            model.addAttribute("turistas", turistas);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao carregar turistas do banco de dados!");
        }
        return "turistas";
    }

    @GetMapping("/turistas/novo")
    public String formNovoTurista(Model model) {
        model.addAttribute("turista", new Turista());
        return "turista-form";
    }

    @PostMapping("/turistas")
    public String salvar(@ModelAttribute Turista turista, Model model) {
        if (turista.getAcompanhantes() != null) {
            turista.getAcompanhantes().removeIf(a -> a == null || a.getNome() == null || a.getNome().isBlank());
        }

        try {
            turistaDAO.inserir(turista);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao salvar turista! Verifique se a identificação já não está cadastrada.");
            model.addAttribute("turista", turista);
            return "turista-form";
        }
        return "redirect:/turistas";
    }

    @GetMapping("/turistas/{id}/editar")
    public String formEditarTurista(@PathVariable int id, Model model) {
        try {
            Turista turista = turistaDAO.buscarPorId(id);
            if (turista == null) {
                return "redirect:/turistas";
            }
            model.addAttribute("turista", turista);
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/turistas";
        }
        return "turista-form";
    }

    @PostMapping("/turistas/{id}")
    public String atualizar(@PathVariable int id, @ModelAttribute Turista turista, Model model) {
        turista.setId(id);
        if (turista.getAcompanhantes() != null) {
            turista.getAcompanhantes().removeIf(a -> a == null || a.getNome() == null || a.getNome().isBlank());
        }

        try {
            turistaDAO.atualizar(turista);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao atualizar turista! Verifique se a identificação já não está cadastrada.");
            model.addAttribute("turista", turista);
            return "turista-form";
        }
        return "redirect:/turistas";
    }

    @PostMapping("/turistas/{id}/deletar")
    public String deletar(@PathVariable int id) {
        try {
            turistaDAO.deletar(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/turistas";
    }
}
