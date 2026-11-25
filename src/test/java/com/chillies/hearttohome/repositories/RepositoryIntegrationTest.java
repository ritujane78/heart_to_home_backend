package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.entity.Contact;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.entity.ServiceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryIntegrationTest {
    @Autowired ContactRepository contactRepository;
    @Autowired ProviderRepository providerRepository;
    @Autowired ServiceRepository serviceRepository;

    @Test void contactRepositoryFiltersByStatus() {
        Contact open = contact("OPEN", "Open Person");
        Contact closed = contact("CLOSED", "Closed Person");
        contactRepository.save(open);
        contactRepository.save(closed);

        var results = contactRepository.findByStatus("OPEN");

        assertEquals(1, results.size());
        assertEquals("Open Person", results.get(0).getName());
    }

    @Test void serviceRepositoryFiltersEnabledServicesByKeywordAndId() {
        ProviderEntity provider = new ProviderEntity();
        provider.setName("Home Care");
        provider = providerRepository.save(provider);

        ServiceEntity enabled = service("CLEAN", "House Cleaning", provider, true);
        ServiceEntity disabled = service("LAWN", "Lawn Care", provider, false);
        enabled = serviceRepository.save(enabled);
        serviceRepository.save(disabled);

        var page = serviceRepository.findByIsEnabledTrueAndTitleContainingIgnoreCase("clean", PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("CLEAN", page.getContent().get(0).getCode());
        assertEquals(1, serviceRepository.findAllByIdInAndIsEnabledTrue(java.util.List.of(enabled.getId(), disabled.getId())).size());
        assertTrue(serviceRepository.existsByTitleIgnoreCase("house cleaning"));
    }

    private Contact contact(String status, String name) {
        Contact c = new Contact();
        c.setName(name);
        c.setEmail("test@example.com");
        c.setCountryCode("977");
        c.setMobileNumber("9800000000");
        c.setMessage("A test contact message");
        c.setStatus(status);
        return c;
    }

    private ServiceEntity service(String code, String title, ProviderEntity provider, boolean enabled) {
        ServiceEntity service = new ServiceEntity();
        service.setCode(code);
        service.setTitle(title);
        service.setDescription("Integration test service");
        service.setPrice(new BigDecimal("25.00"));
        service.setProvider(provider);
        service.setEnabled(enabled);
        return service;
    }
}
