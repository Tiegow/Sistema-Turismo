package br.com.agencia.controller;

import br.com.agencia.model.Colaborador;
import br.com.agencia.model.Passeio;
import br.com.agencia.model.Veiculo;
import br.com.agencia.model.Reserva;
import br.com.agencia.service.PasseioService;
import br.com.agencia.service.ReservaService;
import br.com.agencia.service.RoteiroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/passeios")
public class PasseioController {

    private final PasseioService passeioService;
    private final ReservaService reservaService;
    private final RoteiroService roteiroService;

    public PasseioController(PasseioService passeioService, ReservaService reservaService, RoteiroService roteiroService) {
        this.passeioService = passeioService;
        this.reservaService = reservaService;
        this.roteiroService = roteiroService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("passeios", passeioService.listarTodos());
        return "passeios/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        return carregarFormulario(new Passeio(), model);
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable int id, Model model) {
        try {
            Passeio p = passeioService.buscarPorId(id);
            if (p == null) return "redirect:/passeios";
            return carregarFormulario(p, model);
        } catch (Exception e) {
            return "redirect:/passeios";
        }
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Passeio passeio,
                         @RequestParam(required = false) String dataHoraStr,
                         @RequestParam(required = false) List<Integer> idVeiculos,
                         @RequestParam(required = false) List<Integer> idColaboradores,
                         RedirectAttributes redirect) {
        try {
            if (dataHoraStr != null && !dataHoraStr.isBlank()) {
                passeio.setDataHora(LocalDateTime.parse(dataHoraStr));
            }
            
            if (idVeiculos != null) {
                List<Veiculo> veiculos = new ArrayList<>();
                for (Integer vId : idVeiculos) {
                    Veiculo v = new Veiculo();
                    v.setId(vId);
                    veiculos.add(v);
                }
                passeio.setVeiculosAlocados(veiculos);
            }
            
            if (idColaboradores != null) {
                List<Colaborador> colaboradores = new ArrayList<>();
                for (Integer cId : idColaboradores) {
                    Colaborador c = new Colaborador();
                    c.setId(cId);
                    colaboradores.add(c);
                }
                passeio.setColaboradoresAlocados(colaboradores);
            }

            passeioService.salvar(passeio);
            redirect.addFlashAttribute("sucesso", "Passeio agendado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/passeios";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            passeioService.deletar(id);
            redirect.addFlashAttribute("sucesso", "Passeio cancelado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/passeios";
    }

    @GetMapping("/{id}/manifesto")
    public String gerarManifesto(@PathVariable int id, Model model) {
        try {
            Passeio p = passeioService.buscarPorId(id);
            if (p == null) return "redirect:/passeios";
            
            p.setRoteiro(roteiroService.buscarPorId(p.getRoteiro().getId()));
            
            List<Reserva> reservas = reservaService.buscarReservasCompletasPorPasseio(id);
            
            model.addAttribute("passeio", p);
            model.addAttribute("reservas", reservas);
            
            return "passeios/manifesto";
        } catch (Exception e) {
            return "redirect:/passeios";
        }
    }

    private String carregarFormulario(Passeio passeio, Model model) {
        model.addAttribute("passeio", passeio);
        model.addAttribute("listaRoteiros", passeioService.listarRoteirosDisponiveis());
        model.addAttribute("listaVeiculos", passeioService.listarVeiculosDisponiveis());
        model.addAttribute("listaColaboradores", passeioService.listarColaboradoresDisponiveis());
        return "passeios/form";
    }
}
