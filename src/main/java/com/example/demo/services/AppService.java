package com.example.demo.services;

import com.example.demo.services.models.ExchangeRate;
import com.example.demo.repositories.ExchangeRateRepository;
import com.example.demo.services.models.SimpleExchangeRate;
import com.example.demo.utils.ExcelExporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Element;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppService {

    private final ExchangeRateRepository repository;
    private final RestTemplate restTemplate;

    public AppService(ExchangeRateRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    public ExchangeRate getExchangeRate(String currency) {
        System.out.println("Запит отримано: " + currency); // Друкуємо валюту в логах

        LocalDate today = LocalDate.now();
        Optional<ExchangeRate> existingRate = repository.findByCurrencyAndDate(currency, today);
        if (existingRate.isPresent()) {
            System.out.println("Знайдено в БД: " + existingRate.get()); // Друкуємо знайдений курс
            return existingRate.get();
        }

        // Отримуємо курс валют з ПриватБанку
        String url = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
        var rates = restTemplate.getForObject(url, List.class);
        System.out.println("Отримано з ПриватБанку: " + rates); // Друкуємо API-відповідь

        if (rates != null) {
            for (var obj : rates) {
                var rateMap = (java.util.Map<String, Object>) obj;
                String ccy = (String) rateMap.get("ccy"); // Явно перетворюємо в String
                if (ccy.equalsIgnoreCase(currency)) {
                    double saleRate = Double.parseDouble(rateMap.get("sale").toString());
                    ExchangeRate rate = new ExchangeRate(currency, saleRate, today);
                    repository.save(rate);
                    System.out.println("Збережено в БД: " + rate); // Лог збереження
                    return rate;
                }
            }
        }

        System.out.println("Курс не знайдено");
        return null;
    }



    public List<ExchangeRate> getAllRates() {
        return repository.findAll();
    }

    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<ExchangeRate> rates = repository.findAll();
        ExcelExporter.generateExcel(response, rates);
    }

    // Метод для парсингу новин із Ukr.net
    public List<String> getNewsFromUkrNet() {
        List<String> newsList = new ArrayList<>();
        String url = "https://www.ukr.net/";

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0") // Додаємо userAgent, щоб не блокував сервер
                    .timeout(10_000)
                    .get();

            System.out.println("Отриманий HTML-код:");
            System.out.println(doc.html()); // Друкуємо весь HTML у консоль

        } catch (IOException e) {
            e.printStackTrace();
            newsList.add("Помилка отримання новин!");
        }

        return newsList;
    }


}
