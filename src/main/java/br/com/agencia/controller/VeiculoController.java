package br.com.agencia.controller;

import br.com.agencia.dao.ManutencaoDAO;
import br.com.agencia.dao.VeiculoDAO;
import br.com.agencia.model.Manutencao;
import br.com.agencia.model.Veiculo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
public class VeiculoController {

    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    private final ManutencaoDAO manutencaoDAO = new ManutencaoDAO();

    // ------------------- VEÍCULO -------------------

    @GetMapping("/veiculos")
    public String listar(Model model) {
        try {
            List<Veiculo> veiculos = veiculoDAO.buscarTodos();
            model.addAttribute("veiculos", veiculos);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao carregar veículos do banco de dados!");
        }
        return "veiculos";
    }

    @GetMapping("/veiculos/novo")
    public String formNovo(Model model) {
        model.addAttribute("veiculo", new Veiculo());
        return "veiculo-form";
    }

    @PostMapping("/veiculos")
    public String salvar(@ModelAttribute Veiculo veiculo, Model model) {
        try {
            veiculoDAO.inserir(veiculo);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao salvar veículo! Verifique se a placa já não está cadastrada.");
            model.addAttribute("veiculo", veiculo);
            return "veiculo-form";
        }
        return "redirect:/veiculos";
    }

    @GetMapping("/veiculos/{id}/editar")
    public String formEditar(@PathVariable int id, Model model) {
        try {
            Veiculo veiculo = veiculoDAO.buscarPorId(id);
            if (veiculo == null) {
                return "redirect:/veiculos";
            }
            model.addAttribute("veiculo", veiculo);
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/veiculos";
        }
        return "veiculo-form";
    }

    @PostMapping("/veiculos/{id}")
    public String atualizar(@PathVariable int id, @ModelAttribute Veiculo veiculo, Model model) {
        veiculo.setId(id);
        try {
            veiculoDAO.atualizar(veiculo);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao atualizar veículo! Verifique se a placa já não está cadastrada.");
            model.addAttribute("veiculo", veiculo);
            return "veiculo-form";
        }
        return "redirect:/veiculos/" + id + "/editar";
    }

    @PostMapping("/veiculos/{id}/deletar")
    public String deletar(@PathVariable int id) {
        try {
            veiculoDAO.deletar(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/veiculos";
    }

    // ------------------- MANUTENÇÃO -------------------

    @GetMapping("/veiculos/{idVeiculo}/manutencoes/novo")
    public String formNovaManutencao(@PathVariable int idVeiculo, Model model) {
        return prepararFormManutencao(idVeiculo, new Manutencao(), model, null);
    }

    @PostMapping("/veiculos/{idVeiculo}/manutencoes")
    public String salvarManutencao(@PathVariable int idVeiculo,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String dataEntrada,
                                   @RequestParam(required = false) String dataSaida,
                                   @RequestParam(required = false) String motivo,
                                   @RequestParam(required = false) Integer custo,
                                   Model model) {
        Manutencao m = new Manutencao();
        m.setStatus(status);
        m.setDataEntrada(parseData(dataEntrada));
        m.setDataSaida(parseData(dataSaida));
        m.setMotivo(motivo);
        m.setCusto(custo);

        String erro = validarManutencao(m);
        if (erro != null) {
            return prepararFormManutencao(idVeiculo, m, model, erro);
        }

        try {
            manutencaoDAO.inserir(m, idVeiculo);
        } catch (SQLException e) {
            e.printStackTrace();
            return prepararFormManutencao(idVeiculo, m, model, "Erro ao salvar manutenção!");
        }
        return "redirect:/veiculos/" + idVeiculo + "/editar";
    }

    @GetMapping("/veiculos/{idVeiculo}/manutencoes/{idManutencao}/editar")
    public String formEditarManutencao(@PathVariable int idVeiculo,
                                       @PathVariable int idManutencao,
                                       Model model) {
        try {
            Manutencao m = manutencaoDAO.buscarPorId(idManutencao);
            if (m == null) {
                return "redirect:/veiculos/" + idVeiculo + "/editar";
            }
            return prepararFormManutencao(idVeiculo, m, model, null);
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/veiculos/" + idVeiculo + "/editar";
        }
    }

    @PostMapping("/veiculos/{idVeiculo}/manutencoes/{idManutencao}")
    public String atualizarManutencao(@PathVariable int idVeiculo,
                                      @PathVariable int idManutencao,
                                      @RequestParam(required = false) String status,
                                      @RequestParam(required = false) String dataEntrada,
                                      @RequestParam(required = false) String dataSaida,
                                      @RequestParam(required = false) String motivo,
                                      @RequestParam(required = false) Integer custo,
                                      Model model) {
        Manutencao m = new Manutencao();
        m.setId(idManutencao);
        m.setStatus(status);
        m.setDataEntrada(parseData(dataEntrada));
        m.setDataSaida(parseData(dataSaida));
        m.setMotivo(motivo);
        m.setCusto(custo);

        String erro = validarManutencao(m);
        if (erro != null) {
            return prepararFormManutencao(idVeiculo, m, model, erro);
        }

        try {
            manutencaoDAO.atualizar(m);
        } catch (SQLException e) {
            e.printStackTrace();
            return prepararFormManutencao(idVeiculo, m, model, "Erro ao atualizar manutenção!");
        }
        return "redirect:/veiculos/" + idVeiculo + "/editar";
    }

    @PostMapping("/veiculos/{idVeiculo}/manutencoes/{idManutencao}/deletar")
    public String deletarManutencao(@PathVariable int idVeiculo, @PathVariable int idManutencao) {
        try {
            manutencaoDAO.deletar(idManutencao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "redirect:/veiculos/" + idVeiculo + "/editar";
    }

    // ------------------- Helpers -------------------

    private String prepararFormManutencao(int idVeiculo, Manutencao manutencao, Model model, String erro) {
        try {
            Veiculo veiculo = veiculoDAO.buscarPorId(idVeiculo);
            if (veiculo == null) {
                return "redirect:/veiculos";
            }
            model.addAttribute("veiculo", veiculo);
            model.addAttribute("manutencao", manutencao);
            model.addAttribute("erro", erro);
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/veiculos";
        }
        return "manutencao-form";
    }

    private String validarManutencao(Manutencao m) {
        if (m.getStatus() == null || m.getStatus().isBlank()) {
            return "Status é obrigatório.";
        }
        if (m.getDataEntrada() == null) {
            return "Data de entrada é obrigatória.";
        }
        if (m.getDataSaida() != null && m.getDataSaida().isBefore(m.getDataEntrada())) {
            return "Data de saída não pode ser anterior à data de entrada.";
        }
        return null;
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
