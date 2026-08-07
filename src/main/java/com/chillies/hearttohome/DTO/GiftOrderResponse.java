package com.chillies.hearttohome.DTO;

import com.chillies.hearttohome.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GiftOrderResponse {

    private Long id;

    // Recipient Details
    private String recipientName;
    private String recipientPhone;
    private String relationship;

    // Sender Details (optional if needed elsewhere)
    private String senderName;
    private String senderEmail;

    private String message;

    // Ordered Services
    private List<OrderServiceResponse> services;

    // Payment Details
    private String totalPrice;
    private String currency;
    private BigDecimal exchangeRate;

    // Order Details
    private OrderStatus orderStatus;
    private LocalDateTime orderedAt;
    private LocalDateTime updatedAt;

    // Existing field
    private boolean emailSent;
}