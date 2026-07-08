package com.chillies.hearttohome.DTO;

import com.chillies.hearttohome.models.OrderStatus;

import java.math.BigDecimal;

public record GiftOrderResponse(
        Long id,
        OrderStatus orderStatus,
        BigDecimal totalPrice
) {}
