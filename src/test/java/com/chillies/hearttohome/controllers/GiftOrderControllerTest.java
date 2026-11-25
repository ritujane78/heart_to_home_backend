package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.GiftOrderResponse;
import com.chillies.hearttohome.DTO.PaymentStatusResponse;
import com.chillies.hearttohome.entity.User;
import com.chillies.hearttohome.mapper.ServiceMapper;
import com.chillies.hearttohome.services.CheckoutService;
import com.chillies.hearttohome.services.OrdersService;
import com.chillies.hearttohome.services.PaymentService;
import com.chillies.hearttohome.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftOrderControllerTest {
    @Mock OrdersService ordersService;
    @Mock UserService userService;
    @Mock PaymentService paymentService;
    @Mock ServiceMapper serviceMapper;
    @Mock CheckoutService checkoutService;
    @Mock UserDetails principal;
    @InjectMocks GiftOrderController controller;

    @Test void getsOrdersForAuthenticatedUser() {
        User user = mock(User.class);
        when(principal.getUsername()).thenReturn("asha");
        when(userService.findByUsername("asha")).thenReturn(user);
        when(user.getId()).thenReturn(17L);
        List<GiftOrderResponse> expected = List.of(GiftOrderResponse.builder().id(1L).build());
        when(ordersService.getOrdersByUser(17L)).thenReturn(expected);
        assertSame(expected, controller.getMyOrders(principal).getBody());
    }

    @Test void returnsPaymentStatus() {
        PaymentStatusResponse status = new PaymentStatusResponse("PAID");
        when(paymentService.getPaymentStatus("pi-123")).thenReturn(status);
        assertSame(status, controller.getPaymentStatus("pi-123"));
    }
}
