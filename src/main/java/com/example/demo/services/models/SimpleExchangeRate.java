package com.example.demo.services.models;

public class SimpleExchangeRate {
    private Long id;
    private String currency;
    private double rate;

    public SimpleExchangeRate(Long id, String currency, double rate) {
        this.id = id;
        this.currency = currency;
        this.rate = rate;
    }

    public Long getId() { return id; }
    public String getCurrency() { return currency; }
    public double getRate() { return rate; }
}