package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.GiftOrderRequest;
import com.chillies.hearttohome.DTO.GiftOrderResponse;
import com.chillies.hearttohome.models.GiftOrder;
import com.chillies.hearttohome.models.OrderStatus;
import com.chillies.hearttohome.models.ServiceEntity;
import com.chillies.hearttohome.models.User;
import com.chillies.hearttohome.repositories.OrdersRepository;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final ObjectMapper objectMapper;
    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;

    @Override
    public GiftOrderResponse create(User user, GiftOrderRequest giftOrderRequest) {

//        GiftOrder giftOrder = objectMapper.convertValue(giftOrderRequest, GiftOrder.class);
        GiftOrder order = new GiftOrder();

        order.setSenderName(giftOrderRequest.getSenderName());
        order.setSenderEmail(giftOrderRequest.getSenderEmail());
        order.setRecipientName(giftOrderRequest.getRecipientName());
        order.setRecipientEmail(giftOrderRequest.getRecipientEmail());
        order.setRecipientPhone(giftOrderRequest.getRecipientPhone());
        order.setRelationship(giftOrderRequest.getRelationship());
        order.setMessage(giftOrderRequest.getMessage());
        order.setUser(user);
        order.setTotalPrice(giftOrderRequest.getTotalPrice());
        order.setCurrency(giftOrderRequest.getCurrency());
        // don't trust frontend
        order.setOrderStatus(OrderStatus.IN_PROCESS);

        GiftOrder saved = ordersRepository.save(order);

        GiftOrderResponse response = new GiftOrderResponse(
                saved.getId(),
                saved.getOrderStatus(),
                saved.getTotalPrice()
        );

        return response;
    }
}
