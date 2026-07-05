package br.com.agencia.controller;

import br.com.agencia.dao.ColaboradorDAO;
import br.com.agencia.model.Colaborador;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.SQLException;
import java.util.List;

@Controller
public class ColaboradorController {

    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

    @GetMapping("/colaboradores")
    public String listar(Model model) {
        try {
            List<Colaborador> colaboradores = colaboradorDAO.buscarTodos();
            model.addAttribute("colaboradores", colaboradores);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao carregar colaboradores do banco de dados!");
        }
        return "colaboradores";
    }

    @GetMapping("/colaboradores/novo")
    public String formNovo(Model model) {
        model.addAttribute("colaborador", new Colaborador());
        return "colaborador-form";
    }

    @PostMapping("/colaboradores")
    public String salvar(@ModelAttribute Colaborador colaborador, Model model) {
        try {
            colaboradorDAO.inserir(colaborador);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao salvar colaborador! Verifique se a identificação já não está cadastrada.");
            model.addAttribute("colaborador", colaborador);
            return "colaborador-form";
        }
        return "redirect:/colaboradores";
    }

    @GetMapping("/colaboradores/{id}/editar")
    public String formEditar(@PathVariable int id, Model model) {
        try {
            Colaborador colaborador = colaboradorDAO.buscarPorId(id);
            if (colaborador == null) {
                return "redirect:/colaboradores";
            }
            model.addAttribute("colaborador", colaborador);
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/colaboradores";
        }
        return "colaborador-form";
    }

    @PostMapping("/colaboradores/{id}")
    public String atualizar(@PathVariable int id, @ModelAttribute Colaborador colaborador, Model model) {
        colaborador.setId(id);
        try {
            colaboradorDAO.atualizar(colaborador);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao atualizar colaborador! Verifique se a identificação já não está cadastrada.");
            model.addAttribute("colaborador", colaborador);
            return "colaborador-form";
        }
        return "redirect:/colaboradores";
    }

    @PostMapping("/colaboradores/{id}/deletar")
    public String deletar(@PathVariable int id) {
        try {
            colaboradorDAO.deletar(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/colaboradores";
    }
}
