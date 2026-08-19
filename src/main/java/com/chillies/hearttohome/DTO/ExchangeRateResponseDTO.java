package com.chillies.hearttohome.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class ExchangeRateResponseDTO {

    private Map<String, BigDecimal> rates;

}