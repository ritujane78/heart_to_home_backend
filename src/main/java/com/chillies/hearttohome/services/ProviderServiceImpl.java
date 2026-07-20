package com.chillies.hearttohome.services;

import com.chillies.hearttohome.models.ProviderEntity;
import com.chillies.hearttohome.repositories.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    @Override
    public List<ProviderEntity> getProviders() {
        return providerRepository.findAllByOrderByNameAsc();
    }

    @Override
    public ProviderEntity addProvider(ProviderEntity provider) {

//        provider.setId(provider.getId().trim().toLowerCase());
//        provider.setName(provider.getName().trim());

        if (providerRepository.existsByNameIgnoreCase(provider.getName())) {
            throw new IllegalArgumentException("Provider ID already exists.");
        }

        if (providerRepository.existsByNameIgnoreCase(provider.getName())) {
            throw new IllegalArgumentException("Provider name already exists.");
        }

        return providerRepository.save(provider);
    }
}