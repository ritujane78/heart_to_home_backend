package com.chillies.hearttohome.DTO;

import com.chillies.hearttohome.entity.OrderStatus;

public record GiftOrderResponse(
        Long id,
        OrderStatus orderStatus,
        String totalPrice,
        boolean emailSent,
        String message
) {}