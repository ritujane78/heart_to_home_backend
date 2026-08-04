package com.chillies.hearttohome.services;

import com.chillies.hearttohome.exceptions.BadRequestException;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.exceptions.ConflictException;
import com.chillies.hearttohome.repositories.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ConcurrentModificationException;
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
        if (providerRepository.existsByNameIgnoreCase(provider.getName())) {
            throw new ConflictException(
                    "name",
                    "Provider '" + provider.getName() + "' already exists."
            );
        }

        return providerRepository.save(provider);
    }
}