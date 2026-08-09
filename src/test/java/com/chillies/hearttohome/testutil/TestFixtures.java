package com.chillies.hearttohome.testutil;

import com.chillies.hearttohome.DTO.GiftOrderRequest;
import com.chillies.hearttohome.DTO.PaymentInfoDTO;
import com.chillies.hearttohome.DTO.PaymentInfoRequest;
import com.chillies.hearttohome.DTO.ProviderRequest;
import com.chillies.hearttohome.DTO.ProviderResponse;
import com.chillies.hearttohome.DTO.ServiceDTORequest;
import com.chillies.hearttohome.DTO.ServiceDTOResponse;
import com.chillies.hearttohome.entity.AppRole;
import com.chillies.hearttohome.entity.GiftOrder;
import com.chillies.hearttohome.entity.OrderStatus;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.entity.Role;
import com.chillies.hearttohome.entity.ServiceEntity;
import com.chillies.hearttohome.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class TestFixtures {

    private TestFixtures() {
    }

    public static ProviderEntity provider(Long id, String name) {
        ProviderEntity provider = new ProviderEntity();
        provider.setId(id);
        provider.setName(name);
        return provider;
    }

    public static ProviderRequest providerRequest(String name) {
        ProviderRequest request = new ProviderRequest();
        request.setName(name);
        return request;
    }

    public static ProviderResponse providerResponse(Long id, String name) {
        ProviderResponse response = new ProviderResponse();
        response.setId(id);
        response.setName(name);
        return response;
    }

    public static ServiceEntity service(Long id, String code, String title, ProviderEntity provider, boolean enabled) {
        ServiceEntity service = new ServiceEntity();
        service.setId(id);
        service.setCode(code);
        service.setTitle(title);
        service.setDescription(title + " description");
        service.setPrice(8500.0);
        service.setProvider(provider);
        service.setEnabled(enabled);
        return service;
    }

    public static ServiceDTORequest serviceRequest(Long providerId, String title) {
        ServiceDTORequest request = new ServiceDTORequest();
        request.setProviderId(providerId);
        request.setTitle(title);
        request.setDescription(title + " description");
        request.setPrice(8500.0);
        request.setCode("HS_TEST");
        request.setEnabled(true);
        return request;
    }

    public static ServiceDTOResponse serviceResponse(Long id, Long providerId, String code, String title) {
        ServiceDTOResponse response = new ServiceDTOResponse();
        response.setId(id);
        response.setProviderId(providerId);
        response.setCode(code);
        response.setTitle(title);
        response.setDescription(title + " description");
        response.setPrice(8500.0);
        response.setEnabled(true);
        return response;
    }

    public static Role role(AppRole roleName) {
        Role role = new Role();
        role.setRoleName(roleName);
        return role;
    }

    public static User user(Long id, String username, AppRole roleName) {
        User user = new User(username, username + "@example.com", "encoded-password");
        user.setId(id);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role(roleName));
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        user.setSignupMethod("email");
        return user;
    }

    public static GiftOrderRequest giftOrderRequest(List<Long> serviceIds) {
        GiftOrderRequest request = new GiftOrderRequest();
        request.setRecipientName("Aama");
        request.setRecipientPhone("+9779800000000");
        request.setRelationship("Mom");
        request.setSenderName("Ritu");
        request.setSenderEmail("ritu@example.com");
        request.setMessage("With love");
        request.setServiceIds(serviceIds);
        request.setTotalPrice("USD 63.50");
        request.setCurrency("USD");
        request.setExchangeRate(BigDecimal.valueOf(0.0075));
        return request;
    }

    public static GiftOrder giftOrder(Long id, User user) {
        GiftOrder order = new GiftOrder();
        order.setId(id);
        order.setUser(user);
        order.setRecipientName("Aama");
        order.setRecipientPhone("+9779800000000");
        order.setRelationship("Mom");
        order.setSenderName("Ritu");
        order.setSenderEmail("ritu@example.com");
        order.setTotalPrice("USD 63.50");
        order.setCurrency("USD");
        order.setExchangeRate(BigDecimal.valueOf(0.0075));
        order.setOrderStatus(OrderStatus.IN_PROCESS);
        return order;
    }

    public static PaymentInfoRequest paymentInfoRequest(int amount, String currency) {
        PaymentInfoRequest request = new PaymentInfoRequest();
        request.setAmount(amount);
        request.setCurrency(currency);
        request.setUserEmail("ritu@example.com");
        return request;
    }

    public static PaymentInfoDTO paymentInfoDTO() {
        PaymentInfoDTO dto = new PaymentInfoDTO();
        dto.setPaymentIntentId("pi_test_123");
        dto.setPayerName("Ritu");
        dto.setAmountNpr(BigDecimal.valueOf(8500));
        dto.setTotal("USD 63.50");
        dto.setUserEmail("ritu@example.com");
        return dto;
    }
}
