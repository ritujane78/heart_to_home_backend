package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.*;
import com.chillies.hearttohome.entity.OrderStatus;
import com.chillies.hearttohome.entity.User;
import com.chillies.hearttohome.mapper.ServiceMapper;
import com.chillies.hearttohome.services.CheckoutService;
import com.chillies.hearttohome.services.OrdersService;
import com.chillies.hearttohome.services.PaymentService;
import com.chillies.hearttohome.services.UserService;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class GiftOrderController {

    private final OrdersService ordersService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final ServiceMapper serviceMapper;
    private final CheckoutService checkoutService;


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<AllOrdersDTO>> getAllOrders() {
        return ResponseEntity.ok(ordersService.getAllOrders());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<GiftOrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ordersService.getOrder(id));
    }

    @PostMapping("/validate")
    public ResponseEntity<ServiceValidationResponse> validateServices(
            @RequestBody ServiceValidationRequest request) {

        ServiceValidationResult validation =
                ordersService.validateServices(request.getServiceIds());

        ServiceValidationResponse response =
                new ServiceValidationResponse(
                        validation.valid(),
                        validation.message(),
                        serviceMapper.toDTO(validation.services())
                );

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body)
            throws MessagingException, UnsupportedEncodingException {

        OrderStatus status = OrderStatus.valueOf(body.get("orderStatus"));

        Map<String, Object> response = ordersService.updateStatus(id, status);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<GiftOrderResponse>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(
                ordersService.getOrdersByUser(user.getId())
        );
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            @AuthenticationPrincipal
            UserDetails userDetails,
            @RequestBody
            CheckoutRequest request
    ) throws StripeException {

        User user =
                userService.findByUsername(
                        userDetails.getUsername()
                );

        return ResponseEntity.ok(
                checkoutService.checkout(
                        user,
                        request
                )
        );
    }

    @GetMapping("/payments/{paymentIntentId}/payment-status")
    public PaymentStatusResponse getPaymentStatus(
            @PathVariable String paymentIntentId
    ) {
        return paymentService.getPaymentStatus(
                paymentIntentId
        );
    }
}
