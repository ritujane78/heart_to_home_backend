package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.*;
import com.chillies.hearttohome.entity.*;
import com.chillies.hearttohome.exceptions.BadRequestException;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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

        /*
         * ---------------------------------------------------------
         * 1. Check whether this checkout has already been created.
         * ---------------------------------------------------------
         */
        Optional<Payment> existingPayment =
                paymentService.findByCheckoutId(
                        request.getCheckoutId()
                );

        if (existingPayment.isPresent()) {

            Payment payment = existingPayment.get();

            GiftOrder giftOrder = payment.getGiftOrder();

            /*
             * The checkout already exists.
             *
             * If the payment is still pending, reuse the existing
             * Stripe PaymentIntent instead of creating another one.
             */
            if (payment.getPaymentOrderStatus()
                    == PaymentOrderStatus.PENDING) {

                PaymentIntent paymentIntent =
                        PaymentIntent.retrieve(
                                payment.getPaymentIntentId()
                        );

                /*
                 * Stripe may already have completed the payment even
                 * though the webhook has not updated our database yet.
                 *
                 * In that case, update the existing records instead
                 * of creating new ones.
                 */
                if ("succeeded".equals(
                        paymentIntent.getStatus()
                )) {

                    payment.setPaymentOrderStatus(
                            PaymentOrderStatus.ORDER_SAVED
                    );

                    ordersService.confirmOrder(
                            giftOrder
                    );

                    return new CheckoutResponse(
                            paymentIntent.getClientSecret(),
                            giftOrder.getTotalPrice(),
                            payment.getAmountNpr(),
                            true
                    );
                }

                /*
                 * Payment is still being processed or requires the
                 * customer to complete payment.
                 *
                 * Reuse the existing PaymentIntent.
                 */
                return new CheckoutResponse(
                        paymentIntent.getClientSecret(),
                        giftOrder.getTotalPrice(),
                        payment.getAmountNpr(),
                        false
                );
            }

            /*
             * The payment was already successfully processed.
             *
             * Do not create another payment/order.
             */
            if (payment.getPaymentOrderStatus()
                    == PaymentOrderStatus.ORDER_SAVED) {

                return new CheckoutResponse(
                        null,
                        giftOrder.getTotalPrice(),
                        payment.getAmountNpr(),
                        true
                );
            }

            /*
             * If the previous payment was canceled or failed,
             * allow this checkout to create a new payment.
             *
             * Continue below.
             */
        }

        /*
         * ---------------------------------------------------------
         * 2. Validate services for a new checkout.
         * ---------------------------------------------------------
         */
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

        /*
         * ---------------------------------------------------------
         * 3. Get exchange rate.
         * ---------------------------------------------------------
         */
        ExchangeRateResult exchangeRateResult =
                exchangeRateService.getRate(
                        request.getCurrency()
                );

        BigDecimal exchangeRate =
                exchangeRateResult.rate();

        String currency =
                exchangeRateResult.currency();

        /*
         * ---------------------------------------------------------
         * 4. Calculate original total in NPR.
         * ---------------------------------------------------------
         */
        BigDecimal totalNpr =
                services.stream()
                        .map(ServiceEntity::getPrice)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        /*
         * ---------------------------------------------------------
         * 5. Convert each service individually and round to 2
         *    decimal places.
         * ---------------------------------------------------------
         */
        BigDecimal convertedTotal =
                services.stream()
                        .map(ServiceEntity::getPrice)
                        .map(price ->
                                price
                                        .multiply(exchangeRate)
                                        .setScale(
                                                2,
                                                RoundingMode.HALF_UP
                                        )
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        /*
         * ---------------------------------------------------------
         * 6. Create Stripe PaymentIntent.
         * ---------------------------------------------------------
         */
        PaymentIntent paymentIntent =
                paymentService.createPaymentIntent(
                        convertedTotal,
                        request
                );

        /*
         * ---------------------------------------------------------
         * 7. Create GiftOrder.
         * ---------------------------------------------------------
         */
        GiftOrderRequest orderRequest =
                new GiftOrderRequest();

        orderRequest.setRecipientName(
                request.getRecipientName()
        );

        orderRequest.setRecipientPhone(
                request.getRecipientPhone()
        );

        orderRequest.setRelationship(
                request.getRelationship()
        );

        orderRequest.setSenderName(
                request.getSenderName()
        );

        orderRequest.setSenderEmail(
                request.getSenderEmail()
        );

        orderRequest.setMessage(
                request.getMessage()
        );

        orderRequest.setCurrency(
                currency
        );

        orderRequest.setExchangeRate(
                exchangeRate
        );

        orderRequest.setTotalPrice(
                convertedTotal.toString()
        );

        orderRequest.setServiceIds(
                request.getServiceIds()
        );

        GiftOrder giftOrder =
                ordersService.create(
                        user,
                        orderRequest
                );

        /*
         * ---------------------------------------------------------
         * 8. Save Payment as PENDING.
         * ---------------------------------------------------------
         */
        paymentService.createPendingPayment(
                paymentIntent,
                request,
                currency,
                totalNpr,
                giftOrder
        );

        /*
         * ---------------------------------------------------------
         * 9. Return Stripe client secret.
         * ---------------------------------------------------------
         */
        return new CheckoutResponse(
                paymentIntent.getClientSecret(),
                convertedTotal.toString(),
                totalNpr,
                false
        );
    }

    public ServiceValidationResult validateServices(
            List<Long> serviceIds
    ) {

        List<ServiceEntity> services =
                serviceRepository.findAllByIdInAndIsEnabledTrue(
                        serviceIds
                );

        boolean valid =
                services.size() == serviceIds.size();

        return new ServiceValidationResult(
                valid,
                valid
                        ? "All selected services are available."
                        : "One or more selected services are no longer available. Please refresh the page and try again.",
                services
        );
    }
}