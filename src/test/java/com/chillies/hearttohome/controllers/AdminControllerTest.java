package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.services.ContactService;
import com.chillies.hearttohome.services.Services;
import com.chillies.hearttohome.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    @Mock Services services;
    @Mock UserService userService;
    @Mock ContactService contactService;
    @InjectMocks AdminController controller;

    @BeforeEach void injectAutowiredFields() {
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "contactService", contactService);
    }

    @Test void updatesRoleAndReturnsOk() {
        var response = controller.updateUserRole(6L, "ROLE_ADMIN");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).updateUserRole(6L, "ROLE_ADMIN");
    }

    @Test void closesContactMessage() {
        var response = controller.closeMessage(21L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(contactService).updateMessageStatus(21L, "CLOSED");
        assertEquals("Contact #21 has been closed.", response.getBody().statusMsg());
    }
}
