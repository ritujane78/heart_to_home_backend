package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StripeWebhookControllerTest {
    @Mock PaymentService paymentService;
    @InjectMocks StripeWebhookController controller;

    @Test void forwardsPayloadAndSignature() throws Exception {
        var response = controller.handleWebhook("{\"id\":\"evt_test\"}", "signature");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentService).processWebhook("{\"id\":\"evt_test\"}", "signature");
    }
}
