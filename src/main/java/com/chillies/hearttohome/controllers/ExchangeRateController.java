package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.ExchangeRateResult;
import com.chillies.hearttohome.utils.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exchange-rates")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public ResponseEntity<ExchangeRateResult> getExchangeRate(
            @RequestParam String currency
    ) {
        return ResponseEntity.ok(
                exchangeRateService.getRate(currency)
        );
    }
}