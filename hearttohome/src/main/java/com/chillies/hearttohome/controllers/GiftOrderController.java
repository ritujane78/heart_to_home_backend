package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.GiftOrderRequest;
import com.chillies.hearttohome.DTO.GiftOrderResponse;
import com.chillies.hearttohome.models.GiftOrder;
import com.chillies.hearttohome.models.OrderStatus;
import com.chillies.hearttohome.models.User;
import com.chillies.hearttohome.services.OrdersService;
import com.chillies.hearttohome.services.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class GiftOrderController {

    private final OrdersService ordersService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<GiftOrderResponse> create(@AuthenticationPrincipal UserDetails userDetails, @RequestBody GiftOrderRequest  giftOrderRequest) {
        User user = userService.findByUsername(userDetails.getUsername());
        System.out.println("GiftOrderController create");
        GiftOrderResponse giftOrderResponse = ordersService.create(user, giftOrderRequest);
        return ResponseEntity.ok(giftOrderResponse);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<GiftOrder>> getAllOrders() {
        return ResponseEntity.ok(ordersService.getAllOrders());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<GiftOrder> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        OrderStatus status = OrderStatus.valueOf(body.get("orderStatus"));

        GiftOrder updatedOrder = ordersService.updateStatus(id, status);

        return ResponseEntity.ok(updatedOrder);
    }
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/my-orders")
    public ResponseEntity<List<GiftOrder>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(
                ordersService.getOrdersByUser(user.getId())
        );
    }

}
