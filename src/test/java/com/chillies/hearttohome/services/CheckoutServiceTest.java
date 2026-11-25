package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.CheckoutRequest;
import com.chillies.hearttohome.entity.ServiceEntity;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.utils.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {
    @Mock PaymentService paymentService;
    @Mock ServiceRepository serviceRepository;
    @Mock ExchangeRateService exchangeRateService;
    @Mock OrdersService ordersService;
    @InjectMocks CheckoutService service;

    @Test void validatesRequestedServiceIdsAgainstEnabledServices() {
        ServiceEntity entity = new ServiceEntity();
        entity.setId(3L);
        when(serviceRepository.findAllByIdInAndIsEnabledTrue(List.of(3L))).thenReturn(List.of(entity));

        var result = service.validateServices(List.of(3L));

        assertTrue(result.valid());
        assertEquals(List.of(entity), result.services());
    }

    @Test void reportsUnavailableOrDuplicateServiceIds() {
        when(serviceRepository.findAllByIdInAndIsEnabledTrue(List.of(3L, 4L))).thenReturn(List.of(new ServiceEntity()));
        var result = service.validateServices(List.of(3L, 4L));
        assertFalse(result.valid());
        assertTrue(result.message().contains("no longer available"));
    }

    @Test void rejectsCheckoutBeforeCallingPaymentWhenServiceIsUnavailable() throws Exception {
        CheckoutRequest request = new CheckoutRequest();
        request.setCheckoutId("checkout-1");
        request.setServiceIds(List.of(5L));
        when(paymentService.findByCheckoutId("checkout-1")).thenReturn(java.util.Optional.empty());
        when(serviceRepository.findAllByIdInAndIsEnabledTrue(List.of(5L))).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> service.checkout(null, request));
        verifyNoInteractions(exchangeRateService, ordersService);
        verify(paymentService, never()).createPaymentIntent(any(), any());
    }
}
