package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.*;
import com.chillies.hearttohome.exceptions.BadRequestException;
import com.chillies.hearttohome.exceptions.EmailSendingException;
import com.chillies.hearttohome.exceptions.ResourceNotFoundException;
import com.chillies.hearttohome.entity.*;
import com.chillies.hearttohome.mapper.GiftOrderMapper;
import com.chillies.hearttohome.mapper.OrderServiceMapper;
import com.chillies.hearttohome.mapper.ServiceMapper;
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
    private final GiftOrderMapper giftOrderMapper;
    private final OrderServiceMapper orderServiceMapper;
    private final ServiceMapper serviceMapper;

    @Override
    public GiftOrderResponse create(User user, GiftOrderRequest giftOrderRequest) {

        GiftOrder order = giftOrderMapper.toEntity(giftOrderRequest);

        order.setUser(user);

        ServiceValidationResult validation =
                validateServices(giftOrderRequest.getServiceIds());

        List<ServiceEntity> services = validation.services();

        services.forEach(service ->
                order.getServices().add(
                        orderServiceMapper.toOrderService(service, order)
                ));

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
        GiftOrderResponse response = giftOrderMapper.toResponse(saved);

        response.setEmailSent(emailSent);
        response.setMessage(
                emailSent
                        ? "Order placed successfully."
                        : "Order placed successfully! But we couldn't send the confirmation email."
        );
        return response;
    }

    @Override
    public ServiceValidationResult validateServices(List<Long> serviceIds) {

        List<ServiceEntity> services =
                serviceRepository.findAllByIdInAndIsEnabledTrue(serviceIds);

        boolean valid = services.size() == serviceIds.size();

        return new ServiceValidationResult(
                valid,
                valid
                        ? "All selected services are available."
                        : "One or more selected services are no longer available. Please refresh the page and try again.",
                services
        );
    }

    @Override
    public List<AllOrdersDTO> getAllOrders() {
        return ordersRepository.findAllByOrderByIdDesc();
    }

    @Override
    public GiftOrderResponse getOrder(Long id) {
        GiftOrder giftOrder = ordersRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order",
                                "id",
                                id
                        ));
        return giftOrderMapper.toResponse(giftOrder);
    }

    @Override
    public Map<String, Object> updateStatus(Long id, OrderStatus status) {

        GiftOrder order = ordersRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order",
                                "id",
                                id
                        ));

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
            message = "Status Updated!! But we couldn't send the notification email.";
        }

        return Map.of(
                "order", updatedOrder,
                "message", message,
                "emailSent", emailSent
        );
    }
    @Override
    public List<GiftOrderResponse> getOrdersByUser(Long userId) {
        List<GiftOrder> userOrders = ordersRepository.findByUserIdOrderByIdDesc(userId);
        return giftOrderMapper.toResponse(userOrders);
    }
}
