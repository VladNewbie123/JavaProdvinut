package com.example.demo.controllers;

import ch.qos.logback.core.model.Model;
import com.example.demo.services.models.ExchangeRate;
import com.example.demo.services.AppService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    private final AppService service;

    public ApiController(AppService service) {
        this.service = service;
    }

    @GetMapping("/exchange-rate/{currency}")
    public ResponseEntity<ExchangeRate> getRate(@PathVariable String currency) {
        return ResponseEntity.ok(service.getExchangeRate(currency));
    }

    @GetMapping("/exchange-rate")
    public ResponseEntity<List<ExchangeRate>> getAllRates() {
        return ResponseEntity.ok(service.getAllRates());
    }

    @GetMapping("/exchange-rate/export")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=rates.xlsx");
        service.exportToExcel(response);
    }

    @GetMapping("/news")
    public ResponseEntity<List<String>> getNews() {
        return ResponseEntity.ok(service.getNewsFromUkrNet());
    }
}
