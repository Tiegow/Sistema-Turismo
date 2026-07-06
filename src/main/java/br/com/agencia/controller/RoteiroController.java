package br.com.agencia.controller;

import br.com.agencia.model.Roteiro;
import br.com.agencia.model.RoteiroPreco;
import br.com.agencia.service.RoteiroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/roteiros")
public class RoteiroController {

    private final RoteiroService roteiroService;

    public RoteiroController(RoteiroService roteiroService) {
        this.roteiroService = roteiroService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("roteiros", roteiroService.listarTodos());
        return "roteiros/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("roteiro", new Roteiro());
        return "roteiros/form";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable int id, Model model) {
        try {
            Roteiro roteiro = roteiroService.buscarPorId(id);
            if (roteiro == null) {
                return "redirect:/roteiros";
            }
            RoteiroPreco precoAtual = roteiroService.buscarPrecoAtivo(id);
            model.addAttribute("roteiro", roteiro);
            model.addAttribute("precoAtual", precoAtual != null ? precoAtual.getPreco() : null);
        } catch (Exception e) {
            return "redirect:/roteiros";
        }
        return "roteiros/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Roteiro roteiro, @RequestParam(required = false) Integer preco, RedirectAttributes redirect) {
        try {
            roteiroService.salvar(roteiro, preco);
            redirect.addFlashAttribute("sucesso", "Roteiro salvo com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/roteiros";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            roteiroService.deletar(id);
            redirect.addFlashAttribute("sucesso", "Roteiro deletado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/roteiros";
    }
}
