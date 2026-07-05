package br.com.agencia.controller;

import br.com.agencia.dao.ColaboradorDAO;
import br.com.agencia.dao.GuiaDAO;
import br.com.agencia.dao.MotoristaDAO;
import br.com.agencia.model.Colaborador;
import br.com.agencia.model.Guia;
import br.com.agencia.model.Motorista;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ColaboradorController {

    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();
    private final GuiaDAO guiaDAO = new GuiaDAO();

    @GetMapping("/colaboradores")
    public String listar(Model model) {
        try {
            List<Colaborador> colaboradores = colaboradorDAO.buscarTodos();
            Map<Integer, String> tipos = new HashMap<>();
            Map<Integer, Motorista> motoristas = new HashMap<>();
            Map<Integer, Guia> guias = new HashMap<>();

            for (Colaborador c : colaboradores) {
                Motorista motorista = motoristaDAO.buscarPorId(c.getId());
                Guia guia = guiaDAO.buscarPorId(c.getId());
                if (motorista != null) motoristas.put(c.getId(), motorista);
                if (guia != null) guias.put(c.getId(), guia);

                String tipo;
                if (motorista != null && guia != null) {
                    tipo = "Motorista + Guia";
                } else if (motorista != null) {
                    tipo = "Motorista";
                } else if (guia != null) {
                    tipo = "Guia";
                } else {
                    tipo = "Colaborador";
                }
                tipos.put(c.getId(), tipo);
            }

            model.addAttribute("colaboradores", colaboradores);
            model.addAttribute("tipos", tipos);
            model.addAttribute("motoristas", motoristas);
            model.addAttribute("guias", guias);
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao carregar colaboradores do banco de dados!");
        }
        return "colaboradores";
    }

    @GetMapping("/colaboradores/novo")
    public String formNovo(Model model) {
        model.addAttribute("colaborador", new Colaborador());
        model.addAttribute("motorista", new Motorista());
        model.addAttribute("guia", new Guia());
        model.addAttribute("jaMotorista", false);
        model.addAttribute("jaGuia", false);
        model.addAttribute("formAction", "/colaboradores");
        return "colaborador-form";
    }

    @PostMapping("/colaboradores")
    public String salvar(@ModelAttribute("colaborador") Colaborador colaborador,
                          @ModelAttribute("motorista") Motorista motorista,
                          @ModelAttribute("guia") Guia guia,
                          @RequestParam(defaultValue = "false") boolean ehMotorista,
                          @RequestParam(defaultValue = "false") boolean ehGuia,
                          Model model) {
        limparListasVazias(motorista, guia);

        try {
            colaboradorDAO.inserir(colaborador);
            int id = colaborador.getId();
            motorista.setId(id);
            guia.setId(id);

            try {
                if (ehMotorista) motoristaDAO.tornarMotorista(motorista);
                if (ehGuia) guiaDAO.tornarGuia(guia);
            } catch (SQLException e) {
                colaboradorDAO.deletar(id);
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao salvar colaborador! Verifique se a identificação já não está cadastrada.");
            model.addAttribute("colaborador", colaborador);
            model.addAttribute("motorista", motorista);
            model.addAttribute("guia", guia);
            model.addAttribute("jaMotorista", ehMotorista);
            model.addAttribute("jaGuia", ehGuia);
            model.addAttribute("formAction", "/colaboradores");
            return "colaborador-form";
        }
        return "redirect:/colaboradores";
    }

    @GetMapping("/colaboradores/{id}/editar")
    public String formEditar(@PathVariable int id, Model model) {
        try {
            Colaborador base = colaboradorDAO.buscarPorId(id);
            if (base == null) {
                return "redirect:/colaboradores";
            }

            Motorista motoristaExistente = motoristaDAO.buscarPorId(id);
            Guia guiaExistente = guiaDAO.buscarPorId(id);

            model.addAttribute("colaborador", base);
            model.addAttribute("motorista", motoristaExistente != null ? motoristaExistente : copiarComoMotorista(base));
            model.addAttribute("guia", guiaExistente != null ? guiaExistente : copiarComoGuia(base));
            model.addAttribute("jaMotorista", motoristaExistente != null);
            model.addAttribute("jaGuia", guiaExistente != null);
            model.addAttribute("formAction", "/colaboradores/" + id);
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/colaboradores";
        }
        return "colaborador-form";
    }

    @PostMapping("/colaboradores/{id}")
    public String atualizar(@PathVariable int id,
                             @ModelAttribute("colaborador") Colaborador colaborador,
                             @ModelAttribute("motorista") Motorista motorista,
                             @ModelAttribute("guia") Guia guia,
                             @RequestParam(defaultValue = "false") boolean ehMotorista,
                             @RequestParam(defaultValue = "false") boolean ehGuia,
                             Model model) {
        colaborador.setId(id);
        motorista.setId(id);
        guia.setId(id);
        limparListasVazias(motorista, guia);

        try {
            colaboradorDAO.atualizar(colaborador);

            boolean jaMotorista = motoristaDAO.buscarPorId(id) != null;
            if (ehMotorista) {
                if (jaMotorista) motoristaDAO.atualizarDadosMotorista(motorista);
                else motoristaDAO.tornarMotorista(motorista);
            } else if (jaMotorista) {
                motoristaDAO.removerPapelMotorista(id);
            }

            boolean jaGuia = guiaDAO.buscarPorId(id) != null;
            if (ehGuia) {
                if (jaGuia) guiaDAO.atualizarIdiomas(guia);
                else guiaDAO.tornarGuia(guia);
            } else if (jaGuia) {
                guiaDAO.removerPapelGuia(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao atualizar colaborador! Verifique se a identificação já não está cadastrada.");
            model.addAttribute("colaborador", colaborador);
            model.addAttribute("motorista", motorista);
            model.addAttribute("guia", guia);
            model.addAttribute("jaMotorista", ehMotorista);
            model.addAttribute("jaGuia", ehGuia);
            model.addAttribute("formAction", "/colaboradores/" + id);
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

    private void limparListasVazias(Motorista motorista, Guia guia) {
        if (motorista.getCategoriasCnh() != null) {
            motorista.getCategoriasCnh().removeIf(c -> c == null || c.isBlank());
        }
        if (guia.getIdiomas() != null) {
            guia.getIdiomas().removeIf(i -> i == null || i.isBlank());
        }
    }

    private Motorista copiarComoMotorista(Colaborador base) {
        Motorista m = new Motorista();
        copiarDadosBase(base, m);
        return m;
    }

    private Guia copiarComoGuia(Colaborador base) {
        Guia g = new Guia();
        copiarDadosBase(base, g);
        return g;
    }

    private void copiarDadosBase(Colaborador origem, Colaborador destino) {
        destino.setId(origem.getId());
        destino.setIdentificacao(origem.getIdentificacao());
        destino.setNome(origem.getNome());
        destino.setEmail(origem.getEmail());
        destino.setTelefone(origem.getTelefone());
        destino.setDataContratacao(origem.getDataContratacao());
        destino.setPj(origem.getPj());
    }
}
