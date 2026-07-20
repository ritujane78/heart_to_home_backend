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
import com.chillies.hearttohome.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;
    private final EmailService emailService;

    @Override
    public GiftOrderResponse create(User user, GiftOrderRequest giftOrderRequest) {

        GiftOrder order = new GiftOrder();

        order.setSenderName(giftOrderRequest.getSenderName());
        order.setSenderEmail(giftOrderRequest.getSenderEmail());
        order.setRecipientName(giftOrderRequest.getRecipientName());
        order.setRecipientEmail(giftOrderRequest.getRecipientEmail());
        order.setRecipientPhone(giftOrderRequest.getRecipientPhone());
        order.setRelationship(giftOrderRequest.getRelationship());
        order.setMessage(giftOrderRequest.getMessage());
        order.setUser(user);
        order.setExchangeRate(giftOrderRequest.getExchangeRate());

        List<ServiceEntity> services =
                serviceRepository.findByIdInAndIsEnabledTrue(
                        giftOrderRequest.getServiceIds());

        if (services.size() != giftOrderRequest.getServiceIds().size()) {
            throw new RuntimeException("One or more selected services are no longer available. " +
                    "Please refresh the page and try selecting services again.");
        }

        order.setServiceIds(services);

        order.setTotalPrice(giftOrderRequest.getTotalPrice());
        order.setCurrency(giftOrderRequest.getCurrency());
        // don't trust frontend
        order.setOrderStatus(OrderStatus.IN_PROCESS);

        GiftOrder saved = ordersRepository.save(order);
        if (saved.getId() != null &&
                saved.getSenderEmail() != null &&
                !saved.getSenderEmail().isBlank()) {

            emailService.sendEmailForOrderInitiation(saved.getSenderEmail());
        }

        GiftOrderResponse response = new GiftOrderResponse(
                saved.getId(),
                saved.getOrderStatus(),
                saved.getTotalPrice()
        );

        return response;
    }

    @Override
    public List<GiftOrder> getAllOrders() {
        return ordersRepository.findAllByOrderByIdDesc();
    }

    @Override
    public GiftOrder updateStatus(Long id, OrderStatus status) {

        GiftOrder order = ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(status);
        GiftOrder updatedOrder = ordersRepository.save(order);

        if (updatedOrder.getId() != null &&
                updatedOrder.getSenderEmail() != null &&
                !updatedOrder.getSenderEmail().isBlank()) {

            emailService.sendEmailForOrderStatus(
                    updatedOrder.getSenderEmail(),
                    updatedOrder.getOrderStatus()
            );
        }

        return updatedOrder;
    }
    @Override
    public List<GiftOrder> getOrdersByUser(Long userId) {
        List<GiftOrder> userOrders = ordersRepository.findByUserIdOrderByIdDesc(userId);
        return userOrders;
    }
}
