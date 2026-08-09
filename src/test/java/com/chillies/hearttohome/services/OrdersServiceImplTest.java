package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.GiftOrderRequest;
import com.chillies.hearttohome.DTO.GiftOrderResponse;
import com.chillies.hearttohome.entity.AppRole;
import com.chillies.hearttohome.entity.GiftOrder;
import com.chillies.hearttohome.entity.OrderService;
import com.chillies.hearttohome.entity.OrderStatus;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.entity.ServiceEntity;
import com.chillies.hearttohome.entity.User;
import com.chillies.hearttohome.exceptions.BadRequestException;
import com.chillies.hearttohome.exceptions.EmailSendingException;
import com.chillies.hearttohome.exceptions.ResourceNotFoundException;
import com.chillies.hearttohome.mapper.GiftOrderMapper;
import com.chillies.hearttohome.mapper.OrderServiceMapper;
import com.chillies.hearttohome.repositories.OrdersRepository;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.repositories.UserRepository;
import com.chillies.hearttohome.testutil.TestFixtures;
import com.chillies.hearttohome.util.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersServiceImplTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private GiftOrderMapper giftOrderMapper;

    @Mock
    private OrderServiceMapper orderServiceMapper;

    @InjectMocks
    private OrdersServiceImpl ordersService;

    @Test
    void createValidatesServicesSnapshotsThemAndSendsConfirmationEmail() {
        User user = TestFixtures.user(1L, "ritu", AppRole.ROLE_USER);
        GiftOrderRequest request = TestFixtures.giftOrderRequest(List.of(10L));
        GiftOrder mapped = TestFixtures.giftOrder(null, user);
        GiftOrder saved = TestFixtures.giftOrder(55L, user);
        ProviderEntity provider = TestFixtures.provider(1L, "Clinic Center A");
        ServiceEntity service = TestFixtures.service(10L, "HS_GHC", "General Health Checkup", provider, true);
        OrderService orderService = new OrderService();
        GiftOrderResponse response = GiftOrderResponse.builder().id(55L).build();

        when(giftOrderMapper.toEntity(request)).thenReturn(mapped);
        when(serviceRepository.findAllByIdInAndIsEnabledTrue(List.of(10L))).thenReturn(List.of(service));
        when(orderServiceMapper.toOrderService(service, mapped)).thenReturn(orderService);
        when(ordersRepository.save(mapped)).thenReturn(saved);
        when(giftOrderMapper.toResponse(saved)).thenReturn(response);

        GiftOrderResponse result = ordersService.create(user, request);

        assertThat(mapped.getUser()).isSameAs(user);
        assertThat(mapped.getOrderStatus()).isEqualTo(OrderStatus.IN_PROCESS);
        assertThat(mapped.getServices()).containsExactly(orderService);
        assertThat(result.isEmailSent()).isTrue();
        verify(emailService).sendEmailForOrderInitiation(saved.getServices(), "Ritu", "ritu@example.com");
    }

    @Test
    void createMarksResponseWhenConfirmationEmailFails() {
        User user = TestFixtures.user(1L, "ritu", AppRole.ROLE_USER);
        GiftOrderRequest request = TestFixtures.giftOrderRequest(List.of());
        GiftOrder mapped = TestFixtures.giftOrder(null, user);
        GiftOrder saved = TestFixtures.giftOrder(55L, user);
        GiftOrderResponse response = GiftOrderResponse.builder().id(55L).build();

        when(giftOrderMapper.toEntity(request)).thenReturn(mapped);
        when(serviceRepository.findAllByIdInAndIsEnabledTrue(List.of())).thenReturn(List.of());
        when(ordersRepository.save(mapped)).thenReturn(saved);
        when(giftOrderMapper.toResponse(saved)).thenReturn(response);
        doThrow(new EmailSendingException(
                "mail failed",
                new Exception()))
                .when(emailService)
                .sendEmailForOrderInitiation(saved.getServices(), "Ritu", "ritu@example.com");

        GiftOrderResponse result = ordersService.create(user, request);

        assertThat(result.isEmailSent()).isFalse();
    }

    @Test
    void validateServicesRejectsMissingOrDisabledServices() {
        when(serviceRepository.findAllByIdInAndIsEnabledTrue(List.of(1L, 2L)))
                .thenReturn(List.of(TestFixtures.service(1L, "HS_ONE", "One", TestFixtures.provider(1L, "P"), true)));

        assertThatThrownBy(() -> ordersService.validateServices(List.of(1L, 2L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("no longer available");
    }

    @Test
    void updateStatusPersistsStatusAndSendsEmail() {
        GiftOrder order = TestFixtures.giftOrder(55L, TestFixtures.user(1L, "ritu", AppRole.ROLE_USER));
        when(ordersRepository.findById(55L)).thenReturn(Optional.of(order));
        when(ordersRepository.save(order)).thenReturn(order);

        Map<String, Object> result = ordersService.updateStatus(55L, OrderStatus.DELIVERED);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(result).containsEntry("emailSent", true);
        verify(emailService).sendEmailForOrderStatus(order.getServices(), "Ritu", "ritu@example.com", OrderStatus.DELIVERED);
    }

    @Test
    void updateStatusSkipsEmailWhenSenderEmailIsBlank() {
        GiftOrder order = TestFixtures.giftOrder(55L, TestFixtures.user(1L, "ritu", AppRole.ROLE_USER));
        order.setSenderEmail(" ");
        when(ordersRepository.findById(55L)).thenReturn(Optional.of(order));
        when(ordersRepository.save(order)).thenReturn(order);

        ordersService.updateStatus(55L, OrderStatus.CANCELED);

        verify(emailService, never()).sendEmailForOrderStatus(any(), any(), any(), any());
    }

    @Test
    void getOrderThrowsWhenMissing() {
        when(ordersRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordersService.getOrder(404L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order");
    }
}
