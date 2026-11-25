package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.ProviderRequest;
import com.chillies.hearttohome.DTO.ProviderResponse;
import com.chillies.hearttohome.services.ProviderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderControllerTest {
    @Mock ProviderService providerService;
    @InjectMocks ProviderController controller;

    @Test void listsProviders() {
        List<ProviderResponse> responses = List.of(new ProviderResponse());
        when(providerService.getProviders()).thenReturn(responses);
        assertSame(responses, controller.getProviders());
    }

    @Test void addsProvider() {
        ProviderRequest request = new ProviderRequest();
        ProviderResponse responseBody = new ProviderResponse();
        when(providerService.addProvider(request)).thenReturn(responseBody);
        var response = controller.addProvider(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(responseBody, response.getBody());
    }

    @Test void mapsIllegalArgumentToConflict() {
        ProviderRequest request = new ProviderRequest();
        when(providerService.addProvider(request)).thenThrow(new IllegalArgumentException("duplicate"));
        var response = controller.addProvider(request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("duplicate", response.getBody());
    }
}
