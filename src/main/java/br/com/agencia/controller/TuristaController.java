package br.com.agencia.controller;

import br.com.agencia.dao.TuristaDAO;
import br.com.agencia.model.Turista;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
