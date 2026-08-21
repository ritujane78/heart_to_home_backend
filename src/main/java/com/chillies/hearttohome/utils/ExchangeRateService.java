package com.chillies.hearttohome.utils;

import com.chillies.hearttohome.DTO.ExchangeRateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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

    public ExchangeRateResult getRate(String currency) {

        String normalizedCurrency = currency.toUpperCase();

        if ("NPR".equals(normalizedCurrency)) {
            return new ExchangeRateResult(
                    "NPR",
                    BigDecimal.ONE,
                    false
            );
        }

        try {

            List<Map<String, Object>> response =
                    restClient
                            .get()
                            .uri(EXCHANGE_RATE_URL)
                            .retrieve()
                            .body(List.class);

            if (response != null) {

                for (Map<String, Object> item : response) {

                    String quote = item.get("quote").toString();

                    if (normalizedCurrency.equals(quote)) {

                        BigDecimal rate =
                                new BigDecimal(
                                        item.get("rate").toString()
                                );

                        return new ExchangeRateResult(
                                normalizedCurrency,
                                rate,
                                false
                        );
                    }
                }
            }

        } catch (Exception e) {

            log.warn(
                    "Unable to fetch exchange rate for {}. Using fallback rate.",
                    normalizedCurrency,
                    e
            );
        }

        // External API failed or requested currency was not returned.
        BigDecimal fallbackRate =
                FALLBACK.getOrDefault(
                        normalizedCurrency,
                        BigDecimal.ONE
                );

        return new ExchangeRateResult(
                normalizedCurrency,
                fallbackRate,
                true
        );
    }

    public Map<String, BigDecimal> getRates() {

        Map<String, BigDecimal> rates =
                new HashMap<>(FALLBACK);

        try {

            List<Map<String, Object>> response =
                    restClient
                            .get()
                            .uri(EXCHANGE_RATE_URL)
                            .retrieve()
                            .body(List.class);

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

        } catch (Exception e) {

            log.warn(
                    "Unable to fetch exchange rates. Using fallback rates.",
                    e
            );

            return rates;
        }

        rates.put("NPR", BigDecimal.ONE);

        return rates;
    }
    public String getCurrencySymbol(String currency) {
        return switch (currency.toUpperCase()) {
            case "USD" -> "$";
            case "EUR" -> "€";
            case "GBP" -> "£";
            case "JPY" -> "¥";
            case "INR" -> "₹";
            case "AUD" -> "A$";
            case "CAD" -> "C$";
            default -> currency + " ";
        };
    }
}