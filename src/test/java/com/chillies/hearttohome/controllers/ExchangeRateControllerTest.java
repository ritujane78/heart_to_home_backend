package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.ExchangeRateResult;
import com.chillies.hearttohome.utils.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateControllerTest {
    @Mock ExchangeRateService exchangeRateService;
    @InjectMocks ExchangeRateController controller;

    @Test void returnsRequestedCurrencyRate() {
        ExchangeRateResult rate = new ExchangeRateResult("EUR", new BigDecimal("0.0059"), false);
        when(exchangeRateService.getRate("EUR")).thenReturn(rate);
        var response = controller.getExchangeRate("EUR");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(rate, response.getBody());
    }
}
