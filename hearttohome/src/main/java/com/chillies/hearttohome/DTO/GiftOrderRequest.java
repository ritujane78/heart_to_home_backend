package com.chillies.hearttohome.DTO;

import com.chillies.hearttohome.models.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class GiftOrderRequest {

    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;

    private String relationship;

    private String senderName;
    private String senderEmail;

    private String message;

    private List<String> serviceIds;

    private BigDecimal totalPrice;
    private String currency;

}
