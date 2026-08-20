package com.chillies.hearttohome.DTO;

import java.math.BigDecimal;

public record ExchangeRateResult(
        String currency,
        BigDecimal rate,
        boolean fallback
) {
}