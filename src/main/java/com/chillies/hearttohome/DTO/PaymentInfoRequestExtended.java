package com.chillies.hearttohome.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentInfoRequestExtended {
    private String paymentIntentId;
    private String payerName;
    private BigDecimal amountNpr;
    private String total;
    private String receiptEmail;

}
