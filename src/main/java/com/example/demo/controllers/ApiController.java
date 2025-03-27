package com.example.demo.controllers;

import com.example.demo.models.ExchangeRate;
import com.example.demo.models.Product;
import com.example.demo.services.AppService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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

    @GetMapping("/site-title")
    public ResponseEntity<List<String>> getWebsiteTitle() {
        return ResponseEntity.ok(service.getWebsiteTitle());
    }

    @GetMapping("/parse-products")
    public ResponseEntity<List<Product>> parseProducts() {
        return ResponseEntity.ok(service.parseFoxtrotProducts());
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }
}
