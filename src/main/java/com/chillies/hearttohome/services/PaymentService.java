package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.*;
import com.chillies.hearttohome.entity.*;
import com.chillies.hearttohome.exceptions.EmailSendingException;
import com.chillies.hearttohome.exceptions.PaymentSaveException;
import com.chillies.hearttohome.exceptions.StripePaymentException;
import com.chillies.hearttohome.mapper.GiftOrderMapper;
import com.chillies.hearttohome.mapper.PaymentMapper;
import com.chillies.hearttohome.repositories.PaymentRepository;
import com.chillies.hearttohome.repositories.UserRepository;
import com.chillies.hearttohome.util.NameUtils;
import com.stripe.Stripe;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrdersService ordersService;
    private final UserRepository userRepository;
    private final ExchangeRateService exchangeRateService;

    @Value("${stripe.key.secret}")
    String secretKey;

    @Value("${stripe.webhook.secret}")
    String webhookSecret;

    private final GiftOrderMapper giftOrderMapper;

    public PaymentIntent createPaymentIntent(
            BigDecimal amount,
            CheckoutRequest request,
            BigDecimal totalNpr,
            BigDecimal exchangeRate,
            User user
    ) throws StripeException {
        Stripe.apiKey = secretKey;
        Map<String, Object> metadata =
                new HashMap<>();

        metadata.put(
                "userId",
                user.getId().toString()
        );

        metadata.put(
                "recipientName",
                request.getRecipientName()
        );

        metadata.put(
                "recipientPhone",
                request.getRecipientPhone()
        );

        metadata.put(
                "relationship",
                request.getRelationship()
        );

        metadata.put(
                "senderName",
                request.getSenderName()
        );

        metadata.put(
                "senderEmail",
                request.getSenderEmail()
        );

        metadata.put(
                "message",
                request.getMessage()
        );

        metadata.put(
                "serviceIds",
                request.getServiceIds()
                        .stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","))
        );

        metadata.put(
                "currency",
                request.getCurrency()
        );

        metadata.put(
                "amountNpr",
                totalNpr.toString()
        );

        metadata.put(
                "exchangeRate",
                exchangeRate.toString()
        );
        Map<String, Object> params =
                new HashMap<>();

        String currency = request.getCurrency().toLowerCase();

        long stripeAmount;

        if ("jpy".equals(currency)) {
            stripeAmount = amount.longValue();
        } else {
            stripeAmount = amount
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();
        }

        params.put("amount", stripeAmount);

        params.put("currency", currency);

        params.put(
                "receipt_email",
                request.getSenderEmail()
        );

        params.put(
                "payment_method_types",
                List.of("card")
        );

        params.put(
                "metadata",
                metadata
        );

        return PaymentIntent.create(params);
    }
    @Transactional
    public void processWebhook(
            String payload,
            String signature
    ) throws SignatureVerificationException, EventDataObjectDeserializationException {

        Event event =
                Webhook.constructEvent(
                        payload,
                        signature,
                        webhookSecret
                );

        String eventType = event.getType();

        if (!eventType.startsWith("payment_intent.")) {
            return;
        }

        PaymentIntent paymentIntent =
                (PaymentIntent)
                        event.getDataObjectDeserializer()
                                .deserializeUnsafe();

        Payment payment =
                paymentRepository
                        .findByPaymentIntentId(
                                paymentIntent.getId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Payment not found."
                                )
                        );

        GiftOrder giftOrder =
                payment.getGiftOrder();

        if (giftOrder == null) {
            throw new RuntimeException(
                    "Gift order not found for payment: "
                            + paymentIntent.getId()
            );
        }

        switch (eventType) {

            case "payment_intent.succeeded":

                payment.setPaymentOrderStatus(
                        PaymentOrderStatus.ORDER_SAVED
                );

                ordersService.confirmOrder(
                        giftOrder
                );

                break;

            case "payment_intent.payment_failed":

                payment.setPaymentOrderStatus(
                        PaymentOrderStatus.ORDER_SAVE_FAILED
                );

                giftOrder.setOrderStatus(
                        OrderStatus.CANCELED
                );

                break;

            case "payment_intent.canceled":

                payment.setPaymentOrderStatus(
                        PaymentOrderStatus.ORDER_CANCELED
                );

                giftOrder.setOrderStatus(
                        OrderStatus.CANCELED
                );

                break;

            default:

                return;
        }

        paymentRepository.save(
                payment
        );
    }

    public void createPendingPayment(
            PaymentIntent paymentIntent,
            CheckoutRequest request,
            String currency,
            BigDecimal totalNpr,
            GiftOrder giftOrder

    ) {
        PaymentInfoDTO paymentInfo =
                new PaymentInfoDTO();

        paymentInfo.setPaymentIntentId(
                paymentIntent.getId()
        );

        paymentInfo.setPayerName(
                request.getSenderName()
        );

        paymentInfo.setAmountNpr(
                totalNpr
        );
        BigDecimal total;

        if ("JPY".equalsIgnoreCase(request.getCurrency())) {
            total = BigDecimal.valueOf(paymentIntent.getAmount());
        } else {
            total = BigDecimal.valueOf(paymentIntent.getAmount())
                    .divide(BigDecimal.valueOf(100));
        }

        String currencySymbol = exchangeRateService.getCurrencySymbol(currency);

        paymentInfo.setTotal(
                currencySymbol + total.toPlainString()
        );

        paymentInfo.setUserEmail(
                request.getSenderEmail()
        );

        Payment payment =
                paymentMapper.toEntity(
                        paymentInfo
                );
        payment.setCurrency(currency);
        payment.setGiftOrder(giftOrder);
        payment.setPaymentOrderStatus(
                PaymentOrderStatus.PENDING
        );

        paymentRepository.save(
                payment
        );
    }

    private Payment savePayment(PaymentInfoDTO request) {
        try {
            Payment payment = paymentMapper.toEntity(request);
            payment.setPaymentOrderStatus(PaymentOrderStatus.PENDING);

            return paymentRepository.save(payment);
//            return paymentMapper.toDTO(saved);

        } catch (Exception ex) {
            throw new PaymentSaveException(
                    "Payment was successful, but we were unable to save the payment details. Please contact support.",
                    ex
            );
        }
    }
    public PaymentStatusResponse getPaymentStatus(
            String paymentIntentId
    ) {
        Payment payment =
                paymentRepository
                        .findByPaymentIntentId(paymentIntentId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Payment not found."
                                )
                        );

        return new PaymentStatusResponse(
                payment.getPaymentOrderStatus().name()
        );
    }
}
