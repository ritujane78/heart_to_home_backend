package com.chillies.hearttohome.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class GiftOrderRequest {

    private String recipientName;
    private String recipientPhone;
//    private String recipientEmail;

    private String relationship;

    private String senderName;
    private String senderEmail;

    private String message;

    private List<Long> serviceIds;

    private String totalPrice;
    private String currency;

    private BigDecimal exchangeRate;

    @Override
    public String toString() {
        return "GiftOrderRequest{" +
                "recipientName='" + recipientName + '\'' +
                ", recipientPhone='" + recipientPhone + '\'' +
                ", relationship='" + relationship + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", message='" + message + '\'' +
                ", serviceIds=" + serviceIds +
                ", totalPrice='" + totalPrice + '\'' +
                ", currency='" + currency + '\'' +
                ", exchangeRate=" + exchangeRate +
                '}';
    }
}
