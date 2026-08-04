package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.*;
import com.chillies.hearttohome.entity.GiftOrder;
import com.chillies.hearttohome.entity.OrderStatus;
import com.chillies.hearttohome.entity.Payment;
import com.chillies.hearttohome.entity.User;
import com.chillies.hearttohome.services.OrdersService;
import com.chillies.hearttohome.services.PaymentService;
import com.chillies.hearttohome.services.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @PostMapping
    public ResponseEntity<GiftOrderResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody GiftOrderRequest giftOrderRequest)
            throws MessagingException, UnsupportedEncodingException {

        User user = userService.findByUsername(userDetails.getUsername());

        GiftOrderResponse response = ordersService.create(user, giftOrderRequest);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<AllOrdersDTO>> getAllOrders() {
        return ResponseEntity.ok(ordersService.getAllOrders());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<GiftOrder> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ordersService.getOrder(id));
    }
    @PostMapping("/validate")
    public ResponseEntity<Void> validateServices(@RequestBody ServiceValidationRequest request) {
        ordersService.validateServices(request.getServiceIds());
        return ResponseEntity.ok().build();
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
    public ResponseEntity<List<GiftOrder>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(
                ordersService.getOrdersByUser(user.getId())
        );
    }
    @PostMapping("/payment/secure/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentInfoRequest paymentInfoRequest)
            throws StripeException {

        PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfoRequest);
        String paymentStr = paymentIntent.toJson();

        return new ResponseEntity<>(paymentStr, HttpStatus.OK);
    }
    @PostMapping("/payment/secure/save-payment")
    public ResponseEntity<Payment> savePayment(@RequestBody PaymentInfoRequestExtended paymentInfo){
        return ResponseEntity.ok(paymentService.savePayment(paymentInfo));
    }
}
