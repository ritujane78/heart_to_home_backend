package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.*;
import com.chillies.hearttohome.entity.GiftOrder;
import com.chillies.hearttohome.entity.OrderStatus;
import com.chillies.hearttohome.entity.User;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface OrdersService {


    GiftOrder create(User user, GiftOrderRequest giftOrderRequest);

    ServiceValidationResult validateServices(List<Long> serviceIds);

    List<AllOrdersDTO> getAllOrders();

    GiftOrderResponse getOrder(Long id);

    Map<String, Object> updateStatus(Long id, OrderStatus status) throws MessagingException, UnsupportedEncodingException;

    List<GiftOrderResponse> getOrdersByUser(Long userId);
}
