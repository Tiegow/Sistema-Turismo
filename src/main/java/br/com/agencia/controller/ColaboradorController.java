package br.com.agencia.controller;

import br.com.agencia.model.Colaborador;
import br.com.agencia.model.Guia;
import br.com.agencia.model.Motorista;
import br.com.agencia.service.ColaboradorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/colaboradores")
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    public ColaboradorController(ColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

    @GetMapping
    public String listar(Model model) {
        try {
            List<Colaborador> colaboradores = colaboradorService.listarTodos();
            Map<Integer, String> tipos = new HashMap<>();
            
            for (Colaborador c : colaboradores) {
                boolean ehMotorista = colaboradorService.buscarMotorista(c.getId()) != null;
                boolean ehGuia = colaboradorService.buscarGuia(c.getId()) != null;
                
                if (ehMotorista && ehGuia) tipos.put(c.getId(), "Motorista + Guia");
                else if (ehMotorista) tipos.put(c.getId(), "Motorista");
                else if (ehGuia) tipos.put(c.getId(), "Guia");
                else tipos.put(c.getId(), "Apenas Colaborador");
            }
            model.addAttribute("colaboradores", colaboradores);
            model.addAttribute("tipos", tipos);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar colaboradores.");
        }
        return "colaboradores/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("colaborador", new Colaborador());
        model.addAttribute("motorista", new Motorista());
        model.addAttribute("guia", new Guia());
        model.addAttribute("jaMotorista", false);
        model.addAttribute("jaGuia", false);
        return "colaboradores/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("colaborador") Colaborador colaborador,
                         @ModelAttribute("motorista") Motorista motorista,
                         @ModelAttribute("guia") Guia guia,
                         @RequestParam(defaultValue = "false") boolean ehMotorista,
                         @RequestParam(defaultValue = "false") boolean ehGuia,
                         RedirectAttributes redirect) {
        try {
            colaboradorService.salvar(colaborador, motorista, guia, ehMotorista, ehGuia);
            redirect.addFlashAttribute("sucesso", "Colaborador salvo com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/colaboradores";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable int id, Model model) {
        try {
            Colaborador base = colaboradorService.buscarPorId(id);
            if (base == null) return "redirect:/colaboradores";
            
            Motorista motoristaExistente = colaboradorService.buscarMotorista(id);
            Guia guiaExistente = colaboradorService.buscarGuia(id);

            model.addAttribute("colaborador", base);
            model.addAttribute("motorista", motoristaExistente != null ? motoristaExistente : new Motorista());
            model.addAttribute("guia", guiaExistente != null ? guiaExistente : new Guia());
            model.addAttribute("jaMotorista", motoristaExistente != null);
            model.addAttribute("jaGuia", guiaExistente != null);
        } catch (Exception e) {
            return "redirect:/colaboradores";
        }
        return "colaboradores/form";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            colaboradorService.deletar(id);
            redirect.addFlashAttribute("sucesso", "Colaborador apagado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/colaboradores";
    }
}
