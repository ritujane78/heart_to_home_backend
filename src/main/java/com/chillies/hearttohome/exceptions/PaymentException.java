package com.chillies.hearttohome.exceptions;

public class PaymentException extends AppException {

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}