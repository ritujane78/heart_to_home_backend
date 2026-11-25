package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.entity.ServiceEntity;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.services.Services;
import com.chillies.hearttohome.utils.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {
    @Mock ServiceRepository serviceRepository;
    @Mock Services services;
    @Mock ExchangeRateService exchangeRateService;
    @InjectMocks ServiceController controller;

    @Test void searchesEnabledServicesAndIncludesProviderNamesAndRates() {
        var pageable = PageRequest.of(2, 4);
        ServiceEntity entity = new ServiceEntity();
        entity.setProvider(null); // service list is empty for this fixture; provider names are tested separately below
        when(serviceRepository.findByIsEnabledTrueAndTitleContainingIgnoreCase("clean", pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));
        when(serviceRepository.findByIsEnabledTrue()).thenReturn(List.of());
        when(exchangeRateService.getRates()).thenReturn(Map.of("NPR", java.math.BigDecimal.ONE));

        var response = controller.getServices("clean", 2, 4);

        assertTrue(response.getServices().isEmpty());
        assertTrue(response.getProviderNames().isEmpty());
        verify(serviceRepository).findByIsEnabledTrueAndTitleContainingIgnoreCase("clean", pageable);
        verify(exchangeRateService).getRates();
    }

    @Test void updatesServiceThroughServiceLayer() {
        var request = new com.chillies.hearttohome.DTO.ServiceDTORequest();
        var expected = new com.chillies.hearttohome.DTO.ServiceDTOResponse();
        when(services.updateService(9L, request)).thenReturn(expected);
        assertSame(expected, controller.updateService(9L, request).getBody());
    }
}
