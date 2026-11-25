package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.ContactRequestDto;
import com.chillies.hearttohome.services.ContactService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContactControllerTest {
    @Mock ContactService contactService;
    @InjectMocks ContactController controller;

    @Test void createsContactAndReturnsCreated() {
        ContactRequestDto request = new ContactRequestDto();
        var response = controller.saveContact(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Request processed successfully", response.getBody());
        verify(contactService).saveContact(request);
    }
}
