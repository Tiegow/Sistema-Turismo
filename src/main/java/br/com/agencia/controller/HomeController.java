package br.com.agencia.controller;

import br.com.agencia.model.Passeio;
import br.com.agencia.service.PasseioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final PasseioService passeioService = new PasseioService();

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<Passeio> todos = passeioService.listarTodos();
        
        List<Passeio> comAlertaMotorista = todos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getAlertaMotorista()))
                .collect(Collectors.toList());
                
        List<Passeio> comAlertaGuia = todos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getAlertaGuia()))
                .collect(Collectors.toList());
                
        model.addAttribute("alertasMotorista", comAlertaMotorista);
        model.addAttribute("alertasGuia", comAlertaGuia);
        return "home";
    }
}
