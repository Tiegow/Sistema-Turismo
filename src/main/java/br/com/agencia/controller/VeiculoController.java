package br.com.agencia.controller;

import br.com.agencia.model.Manutencao;
import br.com.agencia.model.Veiculo;
import br.com.agencia.service.VeiculoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("veiculos", veiculoService.listarTodos());
        return "veiculos/lista";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("veiculo", new Veiculo());
        return "veiculos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Veiculo veiculo, RedirectAttributes redirect) {
        try {
            veiculoService.salvar(veiculo);
            redirect.addFlashAttribute("sucesso", "Veículo salvo com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/veiculos";
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable int id, Model model) {
        try {
            Veiculo veiculo = veiculoService.buscarPorId(id);
            if (veiculo == null) return "redirect:/veiculos";
            model.addAttribute("veiculo", veiculo);
        } catch (Exception e) {
            return "redirect:/veiculos";
        }
        return "veiculos/form";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            veiculoService.deletar(id);
            redirect.addFlashAttribute("sucesso", "Veículo deletado com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/veiculos";
    }

    // ------------------- MANUTENÇÃO -------------------

    @GetMapping("/{idVeiculo}/manutencoes/novo")
    public String formNovaManutencao(@PathVariable int idVeiculo, Model model) {
        return prepararFormManutencao(idVeiculo, new Manutencao(), model);
    }

    @GetMapping("/{idVeiculo}/manutencoes/{idManutencao}/editar")
    public String formEditarManutencao(@PathVariable int idVeiculo, @PathVariable int idManutencao, Model model) {
        try {
            Manutencao m = veiculoService.buscarManutencaoPorId(idManutencao);
            if (m == null) return "redirect:/veiculos/" + idVeiculo + "/editar";
            return prepararFormManutencao(idVeiculo, m, model);
        } catch (Exception e) {
            return "redirect:/veiculos/" + idVeiculo + "/editar";
        }
    }

    @PostMapping("/{idVeiculo}/manutencoes/salvar")
    public String salvarManutencao(@PathVariable int idVeiculo,
                                   @RequestParam(required = false) Integer id,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String dataEntrada,
                                   @RequestParam(required = false) String dataSaida,
                                   @RequestParam(required = false) String motivo,
                                   @RequestParam(required = false) Double custo,
                                   RedirectAttributes redirect) {
        Manutencao m = new Manutencao();
        m.setId(id);
        m.setStatus(status);
        m.setDataEntrada(parseData(dataEntrada));
        m.setDataSaida(parseData(dataSaida));
        m.setMotivo(motivo);
        m.setCusto(custo);

        try {
            veiculoService.salvarManutencao(m, idVeiculo);
            redirect.addFlashAttribute("sucesso", "Manutenção registrada com sucesso!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/veiculos/" + idVeiculo + "/editar";
    }

    @PostMapping("/{idVeiculo}/manutencoes/{idManutencao}/deletar")
    public String deletarManutencao(@PathVariable int idVeiculo, @PathVariable int idManutencao, RedirectAttributes redirect) {
        try {
            veiculoService.deletarManutencao(idManutencao);
            redirect.addFlashAttribute("sucesso", "Registro de manutenção apagado!");
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/veiculos/" + idVeiculo + "/editar";
    }

    private String prepararFormManutencao(int idVeiculo, Manutencao manutencao, Model model) {
        try {
            Veiculo veiculo = veiculoService.buscarPorId(idVeiculo);
            if (veiculo == null) return "redirect:/veiculos";
            model.addAttribute("veiculo", veiculo);
            model.addAttribute("manutencao", manutencao);
        } catch (Exception e) {
            return "redirect:/veiculos";
        }
        return "veiculos/manutencao-form";
    }

    private LocalDateTime parseData(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
