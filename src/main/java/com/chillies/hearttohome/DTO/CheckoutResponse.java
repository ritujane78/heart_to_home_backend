package com.chillies.hearttohome.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CheckoutResponse {

    private String clientSecret;

    private String total;

    private BigDecimal amountNpr;
}