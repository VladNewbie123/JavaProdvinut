package com.example.demo.services.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currency;
    private double rate;
    private LocalDate date;

    public ExchangeRate() {}

    public ExchangeRate(String currency, double rate, LocalDate date) {
        this.currency = currency;
        this.rate = rate;
        this.date = date;
    }

    public Long getId() { return id; }
    public String getCurrency() { return currency; }
    public double getRate() { return rate; }
    public LocalDate getDate() { return date; }
}

