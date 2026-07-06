package br.com.agencia.controller;

import br.com.agencia.model.Reserva;
import br.com.agencia.model.Turista;
import br.com.agencia.model.Passeio;
import br.com.agencia.service.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reservas", reservaService.listarTodas());
        return "reservas/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        return carregarFormulario(new Reserva(), model);
    }

    @GetMapping("/{idPasseio}/{idTurista}/editar")
    public String formEditar(@PathVariable int idPasseio, @PathVariable int idTurista, Model model) {
        try {
            Reserva reserva = reservaService.buscarPorId(idPasseio, idTurista);
            if (reserva == null) return "redirect:/reservas";
            return carregarFormulario(reserva, model);
        } catch (Exception e) {
            return "redirect:/reservas";
        }
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Reserva reserva,
                         @RequestParam(required = false) Integer idPasseio,
                         @RequestParam(required = false) Integer idTurista,
                         RedirectAttributes redirect) {
        try {
            if (reserva.getPasseio() == null) reserva.setPasseio(new Passeio());
            if (reserva.getTurista() == null) reserva.setTurista(new Turista());
            
            reserva.getPasseio().setId(idPasseio);
            reserva.getTurista().setId(idTurista);

            reservaService.salvar(reserva);
            redirect.addFlashAttribute("sucesso", "Venda processada com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            if (idPasseio != null && idTurista != null && reservaService.buscarPorId(idPasseio, idTurista) != null) {
                 return "redirect:/reservas/" + idPasseio + "/" + idTurista + "/editar";
            }
            return "redirect:/reservas/novo";
        }
        return "redirect:/reservas";
    }

    @PostMapping("/{idPasseio}/{idTurista}/deletar")
    public String deletar(@PathVariable int idPasseio, @PathVariable int idTurista, RedirectAttributes redirect) {
        try {
            reservaService.deletar(idPasseio, idTurista);
            redirect.addFlashAttribute("sucesso", "Reserva estornada com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/reservas";
    }

    private String carregarFormulario(Reserva reserva, Model model) {
        model.addAttribute("reserva", reserva);
        model.addAttribute("listaPasseios", reservaService.listarPasseiosDisponiveis());
        model.addAttribute("listaTuristas", reservaService.listarTuristas());
        return "reservas/form";
    }
}
