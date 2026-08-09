package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.ServiceDTORequest;
import com.chillies.hearttohome.DTO.ServiceDTOResponse;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.entity.ServiceEntity;
import com.chillies.hearttohome.exceptions.ConflictException;
import com.chillies.hearttohome.exceptions.ResourceNotFoundException;
import com.chillies.hearttohome.mapper.ServiceMapper;
import com.chillies.hearttohome.repositories.ProviderRepository;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.testutil.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicesImplTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private ServicesImpl services;

    @Test
    void addServiceGeneratesInitialsCodeAndAssignsProvider() {
        ProviderEntity provider = TestFixtures.provider(1L, "Clinic Center A");
        ServiceDTORequest request = TestFixtures.serviceRequest(1L, "General Health Checkup");
        ServiceEntity entity = TestFixtures.service(null, null, "General Health Checkup", null, true);
        ServiceEntity saved = TestFixtures.service(2L, "HS_GHC", "General Health Checkup", provider, true);
        ServiceDTOResponse response = TestFixtures.serviceResponse(2L, 1L, "HS_GHC", "General Health Checkup");

        when(serviceRepository.existsByCode("HS_GHC")).thenReturn(false);
        when(serviceMapper.toEntity(request)).thenReturn(entity);
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(serviceRepository.save(entity)).thenReturn(saved);
        when(serviceMapper.toDTO(saved)).thenReturn(response);

        ServiceDTOResponse result = services.addService(request).getBody();

        ArgumentCaptor<ServiceEntity> captor = ArgumentCaptor.forClass(ServiceEntity.class);
        verify(serviceRepository).save(captor.capture());
        assertThat(captor.getValue().getCode()).isEqualTo("HS_GHC");
        assertThat(captor.getValue().getProvider()).isSameAs(provider);
        assertThat(result).isEqualTo(response);
    }

    @Test
    void addServiceAppendsOneUntilGeneratedCodeIsUnique() {
        ProviderEntity provider = TestFixtures.provider(1L, "Clinic Center A");
        ServiceDTORequest request = TestFixtures.serviceRequest(1L, "Health");
        ServiceEntity entity = TestFixtures.service(null, null, "Health", null, true);
        ServiceEntity saved = TestFixtures.service(2L, "HS_H1", "Health", provider, true);

        when(serviceRepository.existsByCode("HS_H")).thenReturn(true);
        when(serviceRepository.existsByCode("HS_H1")).thenReturn(false);
        when(serviceMapper.toEntity(request)).thenReturn(entity);
        when(providerRepository.findById(1L)).thenReturn(Optional.of(provider));
        when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(saved);
        when(serviceMapper.toDTO(saved)).thenReturn(TestFixtures.serviceResponse(2L, 1L, "HS_H1", "Health"));

        services.addService(request);

        verify(serviceRepository).save(argThatServiceWithCode("HS_H1"));
    }

    @Test
    void addServiceThrowsWhenProviderDoesNotExist() {
        ServiceDTORequest request = TestFixtures.serviceRequest(99L, "General Health Checkup");
        when(serviceRepository.existsByCode("HS_GHC")).thenReturn(false);
        when(serviceMapper.toEntity(request))
                .thenReturn(TestFixtures.service(null, null, "General Health Checkup", null, true));
        when(providerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> services.addService(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Provider");
    }

    @Test
    void updateServiceRejectsDuplicateCodeAndTitle() {
        ServiceEntity existing = TestFixtures.service(5L, "HS_OLD", "Old", TestFixtures.provider(1L, "Provider"), true);
        ServiceDTORequest request = TestFixtures.serviceRequest(1L, "New");
        request.setCode("HS_DUP");

        when(serviceRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(serviceRepository.existsByCodeAndIdNot("HS_DUP", 5L)).thenReturn(true);

        assertThatThrownBy(() -> services.updateService(5L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("code");
    }

    @Test
    void deleteServiceDisablesInsteadOfDeleting() {
        ServiceEntity service = TestFixtures.service(5L, "HS_TEST", "Health", TestFixtures.provider(1L, "Provider"), true);
        when(serviceRepository.findById(5L)).thenReturn(Optional.of(service));

        services.deleteService(5L);

        assertThat(service.isEnabled()).isFalse();
        verify(serviceRepository).save(service);
    }

    @Test
    void getDisabledServicesMapsRepositoryResults() {
        ServiceEntity service = TestFixtures.service(5L, "HS_TEST", "Health", TestFixtures.provider(1L, "Provider"), false);
        ServiceDTOResponse response = TestFixtures.serviceResponse(5L, 1L, "HS_TEST", "Health");
        when(serviceRepository.findByIsEnabledFalseOrderByCodeAsc()).thenReturn(List.of(service));
        when(serviceMapper.toDTO(List.of(service))).thenReturn(List.of(response));

        assertThat(services.getDisabledServices()).containsExactly(response);
    }

    private ServiceEntity argThatServiceWithCode(String code) {
        return org.mockito.ArgumentMatchers.argThat(service -> code.equals(service.getCode()));
    }
}
