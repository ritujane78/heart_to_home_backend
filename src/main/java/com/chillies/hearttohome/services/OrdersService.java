package com.chillies.hearttohome.services;

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

public interface OrdersService {


    GiftOrderResponse create(User user, GiftOrderRequest giftOrderRequest) throws MessagingException, UnsupportedEncodingException;

    List<GiftOrder> getAllOrders();

    GiftOrder updateStatus(Long id, OrderStatus status) throws MessagingException, UnsupportedEncodingException;

    List<GiftOrder> getOrdersByUser(Long userId);
}
