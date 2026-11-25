package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.ProviderRequest;
import com.chillies.hearttohome.DTO.ProviderResponse;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.exceptions.ConflictException;
import com.chillies.hearttohome.mapper.ProviderMapper;
import com.chillies.hearttohome.repositories.ProviderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderServiceImplTest {
    @Mock ProviderRepository repository;
    @Mock ProviderMapper mapper;
    @InjectMocks ProviderServiceImpl service;

    @Test void addsProviderWhenNameIsUnique() {
        ProviderRequest request = new ProviderRequest();
        request.setName("Care Co");
        ProviderEntity entity = new ProviderEntity();
        ProviderResponse response = new ProviderResponse();
        when(repository.existsByNameIgnoreCase("Care Co")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        assertSame(response, service.addProvider(request));
        verify(repository).save(entity);
    }

    @Test void rejectsDuplicateProviderNameBeforeSaving() {
        ProviderRequest request = new ProviderRequest();
        request.setName("Care Co");
        when(repository.existsByNameIgnoreCase("Care Co")).thenReturn(true);
        assertThrows(ConflictException.class, () -> service.addProvider(request));
        verify(repository, never()).save(any());
    }
}
