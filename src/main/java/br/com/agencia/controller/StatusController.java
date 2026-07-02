package br.com.agencia.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.agencia.config.ConnectionFactory;

@RestController
public class StatusController {

    @GetMapping("/")
    public Map<String, Object> status() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("app", "UP");

        try (Connection connection = ConnectionFactory.getConnection()) {
            response.put("database", connection.isValid(2) ? "UP" : "DOWN");
        } catch (SQLException e) {
            response.put("database", "DOWN");
            response.put("error", e.getMessage());
        }

        return response;
    }
}
