package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.ProviderResponse;
import com.chillies.hearttohome.services.ProviderService;
import com.chillies.hearttohome.testutil.TestFixtures;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProviderControllerTest {

    private final ProviderService providerService = mock(ProviderService.class);
    private final ProviderController controller = new ProviderController(providerService);

    @Test
    void getProvidersReturnsServiceProviders() {
        ProviderResponse response = TestFixtures.providerResponse(1L, "Clinic Center A");
        when(providerService.getProviders()).thenReturn(List.of(response));

        assertThat(controller.getProviders()).containsExactly(response);
    }

    @Test
    void addProviderReturnsCreatedProviderResponse() {
        ProviderResponse response = TestFixtures.providerResponse(1L, "Clinic Center A");
        when(providerService.addProvider(TestFixtures.providerRequest("Clinic Center A"))).thenReturn(response);

        ResponseEntity<?> result = controller.addProvider(TestFixtures.providerRequest("Clinic Center A"));

        assertThat(result.getBody()).isEqualTo(response);
    }
}
