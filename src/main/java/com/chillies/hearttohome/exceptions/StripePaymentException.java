package com.chillies.hearttohome.exceptions;

public class StripePaymentException extends PaymentException {

    public StripePaymentException(String message) {
        super(message);
    }

    public StripePaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}