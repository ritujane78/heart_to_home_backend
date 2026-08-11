package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.PaymentInfoRequest;
import com.chillies.hearttohome.DTO.PaymentInfoDTO;
import com.chillies.hearttohome.entity.Payment;
import com.chillies.hearttohome.exceptions.PaymentSaveException;
import com.chillies.hearttohome.exceptions.StripePaymentException;
import com.chillies.hearttohome.mapper.PaymentMapper;
import com.chillies.hearttohome.repositories.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Value("${stripe.key.secret}")
    String secretKey;

    public PaymentIntent createPaymentIntent(PaymentInfoRequest paymentInfoRequest) {
        Stripe.apiKey = secretKey;
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentInfoRequest.getAmount());
        params.put("currency", paymentInfoRequest.getCurrency());
        params.put("receipt_email", paymentInfoRequest.getUserEmail());
        params.put("payment_method_types", paymentMethodTypes);

        try {
            return PaymentIntent.create(params);
        } catch (StripeException ex) {

            throw new StripePaymentException(
                    "Unable to process your payment.",
                    ex
            );
        }
    }

    public PaymentInfoDTO savePayment(PaymentInfoDTO request) {
        try {
            Payment payment = paymentMapper.toEntity(request);

            Payment saved = paymentRepository.save(payment);
            return paymentMapper.toDTO(saved);

        } catch (Exception ex) {
            throw new PaymentSaveException(
                    "Payment was successful, but we were unable to save the payment details. Please contact support.",
                    ex
            );
        }
    }
}
