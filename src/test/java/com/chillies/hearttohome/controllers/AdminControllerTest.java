package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.ServiceDTORequest;
import com.chillies.hearttohome.DTO.ServiceDTOResponse;
import com.chillies.hearttohome.services.Services;
import com.chillies.hearttohome.services.UserService;
import com.chillies.hearttohome.testutil.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminControllerTest {

    private Services services;
    private UserService userService;
    private AdminController controller;

    @BeforeEach
    void setUp() {
        services = mock(Services.class);
        userService = mock(UserService.class);
        controller = new AdminController(services);
        ReflectionTestUtils.setField(controller, "userService", userService);
    }

    @Test
    void updateUserRoleDelegatesToUserService() {
        assertThat(controller.updateUserRole(1L, "ROLE_ADMIN").getBody()).isEqualTo("User role updated");
        verify(userService).updateUserRole(1L, "ROLE_ADMIN");
    }

    @Test
    void addServiceDelegatesToServices() {
        ServiceDTORequest request = TestFixtures.serviceRequest(1L, "General Health Checkup");
        ServiceDTOResponse response = TestFixtures.serviceResponse(10L, 1L, "HS_GHC", "General Health Checkup");
        when(services.addService(request)).thenReturn(org.springframework.http.ResponseEntity.ok(response));

        assertThat(controller.addService(request).getBody()).isEqualTo(response);
    }

    @Test
    void disabledAndEnableServiceEndpointsDelegateToServices() {
        ServiceDTOResponse response = TestFixtures.serviceResponse(10L, 1L, "HS_GHC", "General Health Checkup");
        when(services.getDisabledServices()).thenReturn(List.of(response));

        assertThat(controller.getDisabledServices()).containsExactly(response);
        assertThat(controller.enableService(10L).getStatusCode().is2xxSuccessful()).isTrue();
        verify(services).enableService(10L);
    }
}
