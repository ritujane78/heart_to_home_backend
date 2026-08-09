package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.ServiceDTORequest;
import com.chillies.hearttohome.DTO.ServiceDTOResponse;
import com.chillies.hearttohome.DTO.ServicePageResponse;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.entity.ServiceEntity;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.services.Services;
import com.chillies.hearttohome.testutil.TestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceControllerTest {

    private final ServiceRepository serviceRepository = mock(ServiceRepository.class);
    private final Services services = mock(Services.class);
    private final ServiceController controller = new ServiceController(serviceRepository, services);

    @Test
    void getServicesWithoutKeywordReturnsEnabledServicesAndUniqueProviderNames() {
        ProviderEntity providerA = TestFixtures.provider(1L, "Clinic Center A");
        ProviderEntity providerB = TestFixtures.provider(2L, "Clinic Center B");
        ServiceEntity serviceOne = TestFixtures.service(1L, "HS_ONE", "One", providerA, true);
        ServiceEntity serviceTwo = TestFixtures.service(2L, "HS_TWO", "Two", providerA, true);
        ServiceEntity serviceThree = TestFixtures.service(3L, "HS_THREE", "Three", providerB, true);

        when(serviceRepository.findByIsEnabledTrue(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(serviceOne, serviceTwo)));
        when(serviceRepository.findByIsEnabledTrue())
                .thenReturn(List.of(serviceOne, serviceTwo, serviceThree));

        ServicePageResponse response = controller.getServices("", 0, 6);

        assertThat(response.getServices().getContent()).containsExactly(serviceOne, serviceTwo);
        assertThat(response.getProviderNames()).containsExactly("Clinic Center A", "Clinic Center B");
    }

    @Test
    void getServicesWithKeywordUsesCaseInsensitiveTitleSearch() {
        when(serviceRepository.findByIsEnabledTrueAndTitleContainingIgnoreCase(any(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(serviceRepository.findByIsEnabledTrue()).thenReturn(List.of());

        controller.getServices("diabetes", 1, 3);

        verify(serviceRepository).findByIsEnabledTrueAndTitleContainingIgnoreCase(any(String.class), any(Pageable.class));
    }

    @Test
    void updateServiceDelegatesToServiceLayer() {
        ServiceDTORequest request = TestFixtures.serviceRequest(1L, "Updated");
        ServiceDTOResponse response = TestFixtures.serviceResponse(7L, 1L, "HS_UP", "Updated");
        when(services.updateService(7L, request)).thenReturn(response);

        ResponseEntity<?> result = controller.updateService(7L, request);

        assertThat(result.getBody()).isEqualTo(response);
    }
}
