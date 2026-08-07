package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.ProviderRequest;
import com.chillies.hearttohome.DTO.ProviderResponse;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.exceptions.ConflictException;
import com.chillies.hearttohome.mapper.ProviderMapper;
import com.chillies.hearttohome.repositories.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    @Override
    public List<ProviderResponse> getProviders() {
        return providerMapper.toResponseList(
                providerRepository.findAllByOrderByNameAsc()
        );
    }

    @Override
    public ProviderResponse addProvider(ProviderRequest providerRequest) {

        if (providerRepository.existsByNameIgnoreCase(providerRequest.getName())) {
            throw new ConflictException(
                    "name",
                    "Provider '" + providerRequest.getName() + "' already exists."
            );
        }

        ProviderEntity provider = providerMapper.toEntity(providerRequest);

        ProviderEntity savedProvider = providerRepository.save(provider);

        return providerMapper.toResponse(savedProvider);
    }
}