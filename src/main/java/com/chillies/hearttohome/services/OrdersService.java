package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.AllOrdersDTO;
import com.chillies.hearttohome.DTO.GiftOrderRequest;
import com.chillies.hearttohome.DTO.GiftOrderResponse;
import com.chillies.hearttohome.entity.GiftOrder;
import com.chillies.hearttohome.entity.OrderStatus;
import com.chillies.hearttohome.entity.ServiceEntity;
import com.chillies.hearttohome.entity.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface OrdersService {


GiftOrderResponse create(User user, GiftOrderRequest giftOrderRequest);

    List<ServiceEntity> validateServices(List<Long> serviceIds);

    List<AllOrdersDTO> getAllOrders();

    GiftOrder getOrder(Long id);

    Map<String, Object> updateStatus(Long id, OrderStatus status) throws MessagingException, UnsupportedEncodingException;

    List<GiftOrder> getOrdersByUser(Long userId);
}
