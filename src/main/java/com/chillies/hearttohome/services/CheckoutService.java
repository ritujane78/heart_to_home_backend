package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.*;
import com.chillies.hearttohome.entity.*;
import com.chillies.hearttohome.exceptions.BadRequestException;
import com.chillies.hearttohome.mapper.PaymentMapper;
import com.chillies.hearttohome.repositories.PaymentRepository;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final PaymentService paymentService;
    private final ServiceRepository serviceRepository;
    private final ExchangeRateService exchangeRateService;
    private final OrdersService ordersService;

    @Transactional(rollbackFor = StripeException.class)
    public CheckoutResponse checkout(
            User user,
            CheckoutRequest request
    ) throws StripeException {

        ServiceValidationResult validation =
                validateServices(
                        request.getServiceIds()
                );

        if (!validation.valid()) {

            throw new BadRequestException(
                    validation.message()
            );
        }

        List<ServiceEntity> services =
                validation.services();

        BigDecimal rate =
                exchangeRateService.getRate(
                        request.getCurrency()
                );

        BigDecimal totalNpr =
                services.stream()
                        .map(ServiceEntity::getPrice)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal convertedTotal =
                services.stream()
                        .map(ServiceEntity::getPrice)
                        .map(price ->
                                price
                                        .multiply(rate)
                                        .setScale(2, RoundingMode.HALF_UP)
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        PaymentIntent paymentIntent =
                paymentService.createPaymentIntent(
                        convertedTotal,
                        request,
                        totalNpr,
                        rate,
                        user
                        );
        GiftOrderRequest orderRequest = new GiftOrderRequest();

        orderRequest.setRecipientName(request.getRecipientName());
        orderRequest.setRecipientPhone(request.getRecipientPhone());
        orderRequest.setRelationship(request.getRelationship());
        orderRequest.setSenderName(request.getSenderName());
        orderRequest.setSenderEmail(request.getSenderEmail());
        orderRequest.setMessage(request.getMessage());
        orderRequest.setCurrency(request.getCurrency());
        orderRequest.setExchangeRate(rate);
        orderRequest.setTotalPrice(convertedTotal.toString());
        orderRequest.setServiceIds(request.getServiceIds());

        GiftOrder giftOrder =
                ordersService.create(
                        user,
                        orderRequest
                );


        paymentService.createPendingPayment(
                paymentIntent,
                request,
                totalNpr,
                giftOrder
        );

        return new CheckoutResponse(
                paymentIntent.getClientSecret(),
                convertedTotal.toString(),
                totalNpr
        );
    }
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
}
