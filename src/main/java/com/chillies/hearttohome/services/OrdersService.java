package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.AllOrdersDTO;
import com.chillies.hearttohome.DTO.GiftOrderRequest;
import com.chillies.hearttohome.DTO.GiftOrderResponse;
import com.chillies.hearttohome.models.GiftOrder;
import com.chillies.hearttohome.models.OrderStatus;
import com.chillies.hearttohome.models.User;
import jakarta.mail.MessagingException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface OrdersService {


GiftOrderResponse create(User user, GiftOrderRequest giftOrderRequest);
    List<AllOrdersDTO> getAllOrders();

    GiftOrder getOrder(Long id);

    Map<String, Object> updateStatus(Long id, OrderStatus status) throws MessagingException, UnsupportedEncodingException;

    List<GiftOrder> getOrdersByUser(Long userId);
}
