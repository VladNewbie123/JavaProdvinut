package com.example.demo.services;

import com.example.demo.models.ExchangeRate;
import com.example.demo.models.Product;
import com.example.demo.repositories.ExchangeRateRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.utils.ExcelExporter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class AppService {

    private final ExchangeRateRepository repository;
    private final RestTemplate restTemplate;
    private ProductRepository productRepository;

    public AppService(ExchangeRateRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
        this.productRepository = productRepository;
    }

    public ExchangeRate getExchangeRate(String currency) {
        System.out.println("Запит отримано: " + currency);

        LocalDate today = LocalDate.now();
        Optional<ExchangeRate> existingRate = repository.findByCurrencyAndDate(currency, today);
        if (existingRate.isPresent()) {
            System.out.println("Знайдено в БД: " + existingRate.get());
            return existingRate.get();
        }


        String url = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
        var rates = restTemplate.getForObject(url, List.class);
        System.out.println("Отримано з ПриватБанку: " + rates);

        if (rates != null) {
            for (var obj : rates) {
                var rateMap = (java.util.Map<String, Object>) obj;
                String ccy = (String) rateMap.get("ccy"); // Явно перетворюємо в String
                if (ccy.equalsIgnoreCase(currency)) {
                    double saleRate = Double.parseDouble(rateMap.get("sale").toString());
                    ExchangeRate rate = new ExchangeRate(currency, saleRate, today);
                    repository.save(rate);
                    System.out.println("Збережено в БД: " + rate);
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


    public List<String> getWebsiteTitle() {
        String url = "https://www.foxtrot.com.ua/";

        try {
            System.out.println("Запит до сайту: " + url);

            Document doc = Jsoup.connect(url).get();


            String charset = doc.outputSettings().charset().name();
            System.out.println("Використовується кодування: " + charset);

            String title = doc.title();
            System.out.println("Отриманий title: " + title);

            return List.of(title);

        } catch (IOException e) {
            System.out.println("Помилка отримання сторінки!");
            e.printStackTrace();
            return List.of("Помилка отримання назви сайту!");
        }
    }

    public List<Product> parseFoxtrotProducts() {
        String url = "https://www.foxtrot.com.ua/uk/shop/noutbuki-lenovo-loq-15iax9-83gs003cra.html";
        List<Product> productList = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10_000)
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .get();

            System.out.println("Page Charset: " + doc.charset().name());
            for (Element el : doc.getElementsByClass("page__title overflow")) {
                String title = el.text();
                System.out.println("Parsed title: " + title);
                Product product = new Product(title);
                productList.add(product);
                productRepository.save(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productList;
    }
}