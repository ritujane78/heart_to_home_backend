package com.chillies.hearttohome.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeRateService {

    private static final String EXCHANGE_RATE_URL =
            "https://api.frankfurter.dev/v2/rates?base=NPR&quotes=USD,GBP,EUR,AUD,CAD,JPY";

    private final RestClient restClient = RestClient.create();

    private static final Map<String, BigDecimal> FALLBACK =
            Map.of(
                    "USD", new BigDecimal("0.0068"),
                    "GBP", new BigDecimal("0.0050"),
                    "EUR", new BigDecimal("0.0059"),
                    "AUD", new BigDecimal("0.0104"),
                    "CAD", new BigDecimal("0.0093"),
                    "JPY", new BigDecimal("0.9977"),
                    "NPR", BigDecimal.ONE
            );

    public Map<String, BigDecimal> getRates() {

        try {

            List<Map<String, Object>> response =
                    restClient
                            .get()
                            .uri(EXCHANGE_RATE_URL)
                            .retrieve()
                            .body(List.class);

            Map<String, BigDecimal> rates =
                    new HashMap<>(FALLBACK);

            if (response != null) {

                for (Map<String, Object> item : response) {

                    String quote =
                            item.get("quote").toString();

                    BigDecimal rate =
                            new BigDecimal(
                                    item.get("rate").toString()
                            );

                    rates.put(quote, rate);
                }
            }

            rates.put("NPR", BigDecimal.ONE);

            return rates;

        } catch (Exception e) {

            return FALLBACK;
        }
    }

    public BigDecimal getRate(String currency) {

        return getRates()
                .getOrDefault(
                        currency,
                        BigDecimal.ONE
                );
    }
}