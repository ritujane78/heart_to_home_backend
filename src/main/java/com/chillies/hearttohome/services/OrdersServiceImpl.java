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
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
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
    public GiftOrderResponse create(User user, GiftOrderRequest giftOrderRequest){

        GiftOrder order = new GiftOrder();

        order.setSenderName(giftOrderRequest.getSenderName());
        order.setSenderEmail(giftOrderRequest.getSenderEmail());
        order.setRecipientName(giftOrderRequest.getRecipientName());
//        order.setRecipientEmail(giftOrderRequest.getRecipientEmail());
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

        for (ServiceEntity service : services) {

            OrderService orderService = new OrderService();

            orderService.setGiftOrder(order);

            orderService.setService(service);               // optional
//            orderService.setOriginalServiceId(service.getId()); // if using a separate field

            orderService.setCode(service.getCode());
            orderService.setTitle(service.getTitle());
            orderService.setDescription(service.getDescription());
            orderService.setPrice(service.getPrice());
            orderService.setProviderName(service.getProvider().getName());

            order.getServices().add(orderService);
        }

//        order.setServiceIds(services);

        order.setTotalPrice(giftOrderRequest.getTotalPrice());
        order.setCurrency(giftOrderRequest.getCurrency());
        // don't trust frontend
        order.setOrderStatus(OrderStatus.IN_PROCESS);

        GiftOrder saved = ordersRepository.save(order);
        boolean emailSent = true;
        if (saved.getId() != null &&
                saved.getSenderEmail() != null &&
                !saved.getSenderEmail().isBlank()) {

            try {
                emailService.sendEmailForOrderInitiation(
                        saved.getServices(),
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
                        ? "Order placed successfully. A confirmation email has been sent."
                        : "Order placed successfully, but we couldn't send the confirmation email.");
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
            message = updatedOrder.getSenderEmail() != null
                    ? "Status updated successfully. Notification email sent to " + updatedOrder.getSenderEmail() + "."
                    : "Status updated successfully.";
        } else {
            message = "Status updated successfully, but we couldn't send the notification email.";
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
