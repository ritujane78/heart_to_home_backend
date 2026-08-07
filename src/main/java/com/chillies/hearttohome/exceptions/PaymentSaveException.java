package com.chillies.hearttohome.exceptions;

public class PaymentSaveException extends RuntimeException {

    public PaymentSaveException(String message) {
        super(message);
    }

    public PaymentSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}