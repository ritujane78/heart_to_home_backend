package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.ContactRequestDto;
import com.chillies.hearttohome.entity.Contact;
import com.chillies.hearttohome.exceptions.ResourceNotFoundException;
import com.chillies.hearttohome.repositories.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {
    @Mock ContactRepository repository;
    @InjectMocks ContactServiceImpl service;

    @Test void savesNewContactAsOpen() {
        ContactRequestDto request = new ContactRequestDto();
        request.setName("Asha");
        request.setEmail("asha@example.com");
        when(repository.save(any(Contact.class))).thenAnswer(inv -> inv.getArgument(0));

        assertTrue(service.saveContact(request));

        ArgumentCaptor<Contact> captor = ArgumentCaptor.forClass(Contact.class);
        verify(repository).save(captor.capture());
        assertEquals("Asha", captor.getValue().getName());
        assertEquals("OPEN", captor.getValue().getStatus());
    }

    @Test void mapsOpenMessages() {
        Contact contact = new Contact();
        contact.setContactId(12L);
        contact.setName("Asha");
        contact.setEmail("asha@example.com");
        contact.setStatus("OPEN");
        when(repository.findByStatus("OPEN")).thenReturn(List.of(contact));

        var responses = service.getAllOpenMessages();

        assertEquals(1, responses.size());
        assertEquals(12L, responses.get(0).contactId());
        assertEquals("asha@example.com", responses.get(0).email());
    }

    @Test void updatesMessageStatusAndThrowsForMissingContact() {
        Contact contact = new Contact();
        when(repository.findById(4L)).thenReturn(Optional.of(contact));
        service.updateMessageStatus(4L, "CLOSED");
        assertEquals("CLOSED", contact.getStatus());
        verify(repository).save(contact);

        when(repository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateMessageStatus(5L, "CLOSED"));
    }
}
