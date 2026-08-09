package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.ProviderResponse;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.exceptions.ConflictException;
import com.chillies.hearttohome.mapper.ProviderMapper;
import com.chillies.hearttohome.repositories.ProviderRepository;
import com.chillies.hearttohome.testutil.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProviderServiceImplTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ProviderMapper providerMapper;

    @InjectMocks
    private ProviderServiceImpl providerService;

    @Test
    void getProvidersReturnsMappedProvidersInRepositoryOrder() {
        ProviderEntity provider = TestFixtures.provider(1L, "Clinic Center A");
        ProviderResponse response = TestFixtures.providerResponse(1L, "Clinic Center A");
        when(providerRepository.findAllByOrderByNameAsc()).thenReturn(List.of(provider));
        when(providerMapper.toResponseList(List.of(provider))).thenReturn(List.of(response));

        assertThat(providerService.getProviders()).containsExactly(response);
    }

    @Test
    void addProviderRejectsDuplicateNameCaseInsensitively() {
        when(providerRepository.existsByNameIgnoreCase("Clinic Center A")).thenReturn(true);

        assertThatThrownBy(() -> providerService.addProvider(TestFixtures.providerRequest("Clinic Center A")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void addProviderSavesMappedProvider() {
        ProviderEntity unsaved = TestFixtures.provider(null, "Clinic Center A");
        ProviderEntity saved = TestFixtures.provider(10L, "Clinic Center A");
        ProviderResponse response = TestFixtures.providerResponse(10L, "Clinic Center A");
        when(providerRepository.existsByNameIgnoreCase("Clinic Center A")).thenReturn(false);
        when(providerMapper.toEntity(TestFixtures.providerRequest("Clinic Center A"))).thenReturn(unsaved);
        when(providerRepository.save(unsaved)).thenReturn(saved);
        when(providerMapper.toResponse(saved)).thenReturn(response);

        ProviderResponse result = providerService.addProvider(TestFixtures.providerRequest("Clinic Center A"));

        assertThat(result).isEqualTo(response);
        verify(providerRepository).save(unsaved);
    }
}
