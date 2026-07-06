package br.com.agencia.controller;

import br.com.agencia.model.Turista;
import br.com.agencia.service.TuristaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/turistas")
public class TuristaController {

    private final TuristaService turistaService;

    public TuristaController(TuristaService turistaService) {
        this.turistaService = turistaService;
    }

    @GetMapping
    public String listarTuristas(Model model) {
        model.addAttribute("turistas", turistaService.listarTodos());
        return "turistas/lista";
    }

    @GetMapping("/novo")
    public String formNovoTurista(Model model) {
        model.addAttribute("turista", new Turista());
        return "turistas/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Turista turista, RedirectAttributes redirect) {
        try {
            turistaService.salvar(turista);
            redirect.addFlashAttribute("sucesso", "Turista salvo com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/turistas";
    }

    @GetMapping("/{id}/editar")
    public String formEditarTurista(@PathVariable int id, Model model) {
        try {
            Turista turista = turistaService.buscarPorId(id);
            if (turista == null) return "redirect:/turistas";
            model.addAttribute("turista", turista);
        } catch (Exception e) {
            return "redirect:/turistas";
        }
        return "turistas/form";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            turistaService.deletar(id);
            redirect.addFlashAttribute("sucesso", "Turista excluído com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/turistas";
    }
}
