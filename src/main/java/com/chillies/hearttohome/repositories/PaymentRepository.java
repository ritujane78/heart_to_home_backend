package com.chillies.hearttohome.repositories;


import com.chillies.hearttohome.entity.Payment;
import com.chillies.hearttohome.entity.PaymentOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentIntentId(String paymentIntentId);

    Optional<Payment> findByCheckoutId(String checkoutId);

}
