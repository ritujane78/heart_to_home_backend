package com.chillies.hearttohome.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CheckoutRequest {

    private String checkoutId;

    private String recipientName;

    private String recipientPhone;

    private String relationship;

    private String senderName;

    private String senderEmail;

    private String message;

    private List<Long> serviceIds;

    private String currency;
}