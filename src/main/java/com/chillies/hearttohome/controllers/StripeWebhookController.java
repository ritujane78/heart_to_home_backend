package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.services.PaymentService;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(
                    "Stripe-Signature")
            String signature) throws SignatureVerificationException, EventDataObjectDeserializationException {

        paymentService.processWebhook(
                payload,
                signature);

        return ResponseEntity.ok().build();
    }
}