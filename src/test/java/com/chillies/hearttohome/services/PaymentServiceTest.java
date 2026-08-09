package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.PaymentInfoDTO;
import com.chillies.hearttohome.DTO.PaymentInfoRequest;
import com.chillies.hearttohome.entity.Payment;
import com.chillies.hearttohome.exceptions.PaymentSaveException;
import com.chillies.hearttohome.exceptions.StripePaymentException;
import com.chillies.hearttohome.mapper.PaymentMapper;
import com.chillies.hearttohome.repositories.PaymentRepository;
import com.chillies.hearttohome.testutil.TestFixtures;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "secretKey", "sk_test_unit");
    }

    @Test
    void createPaymentIntentPassesAmountCurrencyAndCardPaymentMethodToStripe() {
        PaymentInfoRequest request = TestFixtures.paymentInfoRequest(1, "usd");
        PaymentIntent intent = mock(PaymentIntent.class);

        try (MockedStatic<PaymentIntent> paymentIntent = mockStatic(PaymentIntent.class)) {
            paymentIntent.when(() -> PaymentIntent.create(any(Map.class)))
                    .thenAnswer(invocation -> {
                        Map<String, Object> params = invocation.getArgument(0);
                        assertThat(params)
                                .containsEntry("amount", 1)
                                .containsEntry("currency", "usd")
                                .containsEntry("payment_method_types", List.of("card"));
                        return intent;
                    });

            assertThat(paymentService.createPaymentIntent(request)).isSameAs(intent);
        }

        assertThat(Stripe.apiKey).isEqualTo("sk_test_unit");
    }

    @Test
    void createPaymentIntentWrapsStripeException() {
        PaymentInfoRequest request = TestFixtures.paymentInfoRequest(6350, "usd");
        StripeException stripeException = mock(StripeException.class);

        try (MockedStatic<PaymentIntent> paymentIntent = mockStatic(PaymentIntent.class)) {
            paymentIntent.when(() -> PaymentIntent.create(any(Map.class))).thenThrow(stripeException);

            assertThatThrownBy(() -> paymentService.createPaymentIntent(request))
                    .isInstanceOf(StripePaymentException.class)
                    .hasMessageContaining("Unable to process your payment");
        }
    }

    @Test
    void savePaymentPersistsMappedPaymentAndReturnsDto() {
        PaymentInfoDTO request = TestFixtures.paymentInfoDTO();
        Payment payment = new Payment();
        payment.setPaymentIntentId("pi_test_123");
        payment.setPayerName("Ritu");
        payment.setAmountNpr(BigDecimal.valueOf(8500));
        payment.setTotal("USD 63.50");
        payment.setUserEmail("ritu@example.com");

        Payment saved = new Payment();
        saved.setId(10L);
        saved.setPaymentIntentId("pi_test_123");
        saved.setPayerName("Ritu");

        saved.setAmountNpr(BigDecimal.valueOf(8500));
        saved.setTotal("USD 63.50");
        saved.setUserEmail("ritu@example.com");

        when(paymentMapper.toEntity(request)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(saved);
        when(paymentMapper.toDTO(saved)).thenReturn(request);

        assertThat(paymentService.savePayment(request)).isEqualTo(request);
        verify(paymentRepository).save(payment);
    }

    @Test
    void savePaymentWrapsPersistenceErrors() {
        PaymentInfoDTO request = TestFixtures.paymentInfoDTO();
        when(paymentMapper.toEntity(request)).thenThrow(new IllegalStateException("mapping failed"));

        assertThatThrownBy(() -> paymentService.savePayment(request))
                .isInstanceOf(PaymentSaveException.class)
                .hasMessageContaining("unable to save");
    }
}
