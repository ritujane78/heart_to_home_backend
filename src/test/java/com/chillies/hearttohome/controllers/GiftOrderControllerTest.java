package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.GiftOrderRequest;
import com.chillies.hearttohome.DTO.GiftOrderResponse;
import com.chillies.hearttohome.DTO.PaymentInfoDTO;
import com.chillies.hearttohome.DTO.PaymentInfoRequest;
import com.chillies.hearttohome.DTO.ServiceValidationRequest;
import com.chillies.hearttohome.entity.AppRole;
import com.chillies.hearttohome.entity.OrderStatus;
import com.chillies.hearttohome.entity.User;
import com.chillies.hearttohome.services.OrdersService;
import com.chillies.hearttohome.services.PaymentService;
import com.chillies.hearttohome.services.UserService;
import com.chillies.hearttohome.testutil.TestFixtures;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GiftOrderControllerTest {

    private final OrdersService ordersService = mock(OrdersService.class);
    private final UserService userService = mock(UserService.class);
    private final PaymentService paymentService = mock(PaymentService.class);
    private final GiftOrderController controller = new GiftOrderController(ordersService, userService, paymentService);

    @Test
    void createLooksUpAuthenticatedUserAndCreatesOrder() throws Exception {
        UserDetails userDetails = mock(UserDetails.class);
        User user = TestFixtures.user(1L, "ritu", AppRole.ROLE_USER);
        GiftOrderRequest request = TestFixtures.giftOrderRequest(List.of(10L));
        GiftOrderResponse response = GiftOrderResponse.builder().id(55L).build();

        when(userDetails.getUsername()).thenReturn("ritu");
        when(userService.findByUsername("ritu")).thenReturn(user);
        when(ordersService.create(user, request)).thenReturn(response);

        assertThat(controller.create(userDetails, request).getBody()).isEqualTo(response);
    }

    @Test
    void validateServicesDelegatesToOrderService() {
        ServiceValidationRequest request = new ServiceValidationRequest();
        request.setServiceIds(List.of(1L, 2L));

        ResponseEntity<Void> response = controller.validateServices(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(ordersService).validateServices(List.of(1L, 2L));
    }

    @Test
    void updateStatusParsesEnumAndReturnsServiceResponse() throws Exception {
        Map<String, Object> serviceResponse = Map.of("emailSent", true);
        when(ordersService.updateStatus(5L, OrderStatus.DELIVERED)).thenReturn(serviceResponse);

        assertThat(controller.updateStatus(5L, Map.of("orderStatus", "DELIVERED")).getBody())
                .isEqualTo(serviceResponse);
    }

    @Test
    void createPaymentIntentReturnsStripeJson() throws Exception {
        PaymentInfoRequest request = TestFixtures.paymentInfoRequest(6350, "usd");
        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.toJson()).thenReturn("{\"id\":\"pi_test_123\"}");
        when(paymentService.createPaymentIntent(request)).thenReturn(intent);

        assertThat(controller.createPaymentIntent(request).getBody()).isEqualTo("{\"id\":\"pi_test_123\"}");
    }

    @Test
    void savePaymentReturnsSavedPaymentInfo() {
        PaymentInfoDTO dto = TestFixtures.paymentInfoDTO();
        when(paymentService.savePayment(dto)).thenReturn(dto);

        assertThat(controller.savePayment(dto).getBody()).isEqualTo(dto);
    }
}
