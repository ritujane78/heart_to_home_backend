package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.AllOrdersDTO;
import com.chillies.hearttohome.DTO.GiftOrderRequest;
import com.chillies.hearttohome.DTO.GiftOrderResponse;
import com.chillies.hearttohome.exceptions.EmailSendingException;
import com.chillies.hearttohome.models.*;
import com.chillies.hearttohome.repositories.OrdersRepository;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.repositories.UserRepository;
import com.chillies.hearttohome.util.EmailService;
import com.chillies.hearttohome.util.NameUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
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
        order.setRecipientPhone(giftOrderRequest.getRecipientPhone());
        order.setRelationship(giftOrderRequest.getRelationship());
        order.setMessage(giftOrderRequest.getMessage());
        order.setUser(user);
        order.setExchangeRate(giftOrderRequest.getExchangeRate());

        List<ServiceEntity> services =
                validateServices(giftOrderRequest.getServiceIds());

        for (ServiceEntity service : services) {

            OrderService orderService = new OrderService();

            orderService.setGiftOrder(order);
            orderService.setService(service);
            orderService.setCode(service.getCode());
            orderService.setTitle(service.getTitle());
            orderService.setDescription(service.getDescription());
            orderService.setPrice(service.getPrice());
            orderService.setProviderName(service.getProvider().getName());

            order.getServices().add(orderService);
        }

        order.setTotalPrice(giftOrderRequest.getTotalPrice());
        order.setCurrency(giftOrderRequest.getCurrency());
        order.setOrderStatus(OrderStatus.IN_PROCESS);

        GiftOrder saved = ordersRepository.save(order);

        boolean emailSent = true;
        if (saved.getId() != null &&
                saved.getSenderEmail() != null &&
                !saved.getSenderEmail().isBlank()) {

            try {
                emailService.sendEmailForOrderInitiation(
                        saved.getServices(),
                        NameUtils.formatFirstName(saved.getSenderName()),
                        saved.getSenderEmail()
                );
            } catch (EmailSendingException ex) {
                emailSent = false;
                log.error("Unable to send order initiation email for order {}", saved.getId(), ex);
            }
        }

        return new GiftOrderResponse(
                saved.getId(),
                saved.getOrderStatus(),
                saved.getTotalPrice(),
                emailSent,
                emailSent
                        ? "Order placed successfully."
                        : "Successful! But we couldn't send the confirmation email."
        );
    }

    @Override
    public List<ServiceEntity> validateServices(List<Long> serviceIds) {
        List<ServiceEntity> services =
                serviceRepository.findAllByIdInAndIsEnabledTrue(serviceIds);

        if (services.size() != serviceIds.size()) {
            throw new RuntimeException(
                    "One or more selected services are no longer available. " +
                            "Please refresh the page and try selecting services again."
            );
        }

        return services;
    }

    @Override
    public List<AllOrdersDTO> getAllOrders() {
        return ordersRepository.findAllByOrderByIdDesc();
    }

    @Override
    public GiftOrder getOrder(Long id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Map<String, Object> updateStatus(Long id, OrderStatus status) {

        GiftOrder order = ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(status);
        GiftOrder updatedOrder = ordersRepository.save(order);

        boolean emailSent = true;

        if (updatedOrder.getSenderEmail() != null &&
                !updatedOrder.getSenderEmail().isBlank()) {

            try {
                emailService.sendEmailForOrderStatus(
                        updatedOrder.getServices(),
                        NameUtils.formatFirstName(updatedOrder.getSenderName()),
                        updatedOrder.getSenderEmail(),
                        updatedOrder.getOrderStatus()
                );
            } catch (EmailSendingException ex) {
                emailSent = false;
                log.error(
                        "Unable to send order status email for order {}",
                        updatedOrder.getId(),
                        ex
                );
            }
        }

        String message;

        if (emailSent) {
            message = "Status updated successfully. Email sent to " + updatedOrder.getSenderEmail() + ".";
        } else {
            message = "Updated!! But we couldn't send the notification email.";
        }

        return Map.of(
                "order", updatedOrder,
                "message", message,
                "emailSent", emailSent
        );
    }
    @Override
    public List<GiftOrder> getOrdersByUser(Long userId) {
        List<GiftOrder> userOrders = ordersRepository.findByUserIdOrderByIdDesc(userId);
        return userOrders;
    }
}
