package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.entity.AppRole;
import com.chillies.hearttohome.entity.GiftOrder;
import com.chillies.hearttohome.entity.OrderStatus;
import com.chillies.hearttohome.entity.Payment;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.entity.Role;
import com.chillies.hearttohome.entity.ServiceEntity;
import com.chillies.hearttohome.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RepositoryIntegrationTest {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private ProviderEntity providerA;
    private ProviderEntity providerB;

    @BeforeEach
    void setUp() {
        ordersRepository.deleteAll();
        paymentRepository.deleteAll();
        serviceRepository.deleteAll();
        providerRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        providerA = provider("Clinic Center A");
        providerB = provider("Clinic Center B");
        providerRepository.saveAll(List.of(providerB, providerA));
    }

    @Test
    void providerRepositoryFindsProvidersByNameIgnoringCaseAndOrderingByName() {
        assertThat(providerRepository.existsByNameIgnoreCase("clinic center a")).isTrue();
        assertThat(providerRepository.findByNameIgnoreCase("CLINIC CENTER B")).contains(providerB);
        assertThat(providerRepository.findAllByOrderByNameAsc())
                .extracting(ProviderEntity::getName)
                .containsExactly("Clinic Center A", "Clinic Center B");
    }

    @Test
    void serviceRepositoryFiltersEnabledServicesAndSearchesTitleIgnoringCase() {
        ServiceEntity enabled = service("HS_GHC", "General Health Checkup", providerA, true);
        ServiceEntity disabled = service("HS_OLD", "Old Disabled Service", providerB, false);
        serviceRepository.saveAll(List.of(enabled, disabled));

        assertThat(serviceRepository.findByIsEnabledTrue()).containsExactly(enabled);
        assertThat(serviceRepository.findByIsEnabledFalseOrderByCodeAsc()).containsExactly(disabled);
        assertThat(serviceRepository.findByIsEnabledTrueAndTitleContainingIgnoreCase("general"))
                .containsExactly(enabled);
        assertThat(serviceRepository.findAllByIdInAndIsEnabledTrue(List.of(enabled.getId(), disabled.getId())))
                .containsExactly(enabled);
    }

    @Test
    void userAndOrderRepositoriesPersistRelationships() {
        Role role = roleRepository.save(new Role(AppRole.ROLE_USER));
        User user = new User("ritu", "ritu@example.com", "encoded");
        user.setFirstName("Ritu");
        user.setLastName("Shrestha");
        user.setRole(role);
        userRepository.save(user);

        GiftOrder order = new GiftOrder();
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
        ordersRepository.save(order);

        assertThat(userRepository.findByUsername("ritu")).contains(user);
        assertThat(userRepository.findByEmail("ritu@example.com")).contains(user);
        assertThat(ordersRepository.findByUserIdOrderByIdDesc(user.getId())).containsExactly(order);
    }

    @Test
    void paymentRepositoryPersistsPaymentDetails() {
        Payment payment = new Payment();
        payment.setPaymentIntentId("pi_test_123");
        payment.setUserEmail("ritu@example.com");
        payment.setTotal("USD 63.50");
        payment.setAmountNpr(BigDecimal.valueOf(8500));
        payment.setPayerName("Ritu");

        Payment saved = paymentRepository.save(payment);

        assertThat(paymentRepository.findById(saved.getId()))
                .get()
                .extracting(Payment::getPaymentIntentId, Payment::getUserEmail, Payment::getPayerName)
                .containsExactly("pi_test_123", "ritu@example.com", "Ritu");
    }

    private ProviderEntity provider(String name) {
        ProviderEntity provider = new ProviderEntity();
        provider.setName(name);
        return provider;
    }

    private ServiceEntity service(String code, String title, ProviderEntity provider, boolean enabled) {
        ServiceEntity service = new ServiceEntity();
        service.setCode(code);
        service.setTitle(title);
        service.setDescription(title + " description");
        service.setPrice(8500.0);
        service.setProvider(provider);
        service.setEnabled(enabled);
        return service;
    }
}
