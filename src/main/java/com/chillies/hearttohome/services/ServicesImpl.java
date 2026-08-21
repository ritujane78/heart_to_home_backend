package com.chillies.hearttohome.services;


import com.chillies.hearttohome.DTO.ServiceDTORequest;
import com.chillies.hearttohome.DTO.ServiceDTOResponse;
import com.chillies.hearttohome.DTO.ServicePageResponseDTO;
import com.chillies.hearttohome.exceptions.ConflictException;
import com.chillies.hearttohome.exceptions.ResourceNotFoundException;
import com.chillies.hearttohome.entity.ProviderEntity;
import com.chillies.hearttohome.entity.ServiceEntity;
import com.chillies.hearttohome.mapper.ServiceMapper;
import com.chillies.hearttohome.repositories.ProviderRepository;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.utils.ExchangeRateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicesImpl implements Services {

    private final ServiceRepository serviceRepository;
    private final ProviderRepository providerRepository;
    private final ServiceMapper serviceMapper;
    private final ExchangeRateService exchangeRateService;


    @Override
    public ServicePageResponseDTO getServices(
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id")
                );

        Page<ServiceEntity> services =
                serviceRepository.findAll(pageable);

        List<String> providerNames =
                providerRepository.findAll()
                        .stream()
                        .map(ProviderEntity::getName)
                        .toList();

        return new ServicePageResponseDTO(
                services,
                providerNames,
                exchangeRateService.getRates()
        );
    }

    @Override
    public ResponseEntity<ServiceDTOResponse> addService(ServiceDTORequest serviceDTORequest) {

        String title = serviceDTORequest.getTitle().trim();

        StringBuilder codeBuilder = new StringBuilder("HS_");

        String[] words = title.split("\\s+");

        if (words.length == 1) {
            codeBuilder.append(Character.toUpperCase(words[0].charAt(0)));
        } else {
            for (String word : words) {
                codeBuilder.append(Character.toUpperCase(word.charAt(0)));
            }
        }

        String code = codeBuilder.toString();

        // Keep appending "1" until a unique code is found
        while (serviceRepository.existsByCode(code)) {
            code += "1";
        }

        ServiceEntity serviceEntity = serviceMapper.toEntity(serviceDTORequest);
        serviceEntity.setCode(code);

        ProviderEntity provider = providerRepository.findById(serviceDTORequest.getProviderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Provider",
                                "id",
                                serviceDTORequest.getProviderId()
                        ));

        serviceEntity.setProvider(provider);

        ServiceEntity saved = serviceRepository.save(serviceEntity);

        return ResponseEntity.ok(serviceMapper.toDTO(saved));
    }
    @Override
    public boolean titleExists(String title){

        if (serviceRepository.existsByTitleIgnoreCase(title)) {
            return true;
        }
        return false;

    }
    @Transactional
    @Override
    public void deleteService(Long id) {

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Service",
                                "id",
                                id
                        ));

        service.setEnabled(false);

        serviceRepository.save(service);
    }
    @Override
    public List<ServiceDTOResponse> getDisabledServices() {
        return serviceMapper.toDTO(serviceRepository.findByIsEnabledFalseOrderByCodeAsc());
    }

    @Override
    @Transactional
    public void enableService(Long id) {

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Service",
                                "id",
                                id
                        ));

        service.setEnabled(true);

        serviceRepository.save(service);
    }

    @Override
    public ServiceDTOResponse updateService(
            Long id,
            ServiceDTORequest request) {

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Service",
                                "id",
                                id
                        ));
        boolean codeExists = serviceRepository.existsByCodeAndIdNot(request.getCode(), id);

        if (codeExists) {
            throw new ConflictException("code", "A service with this code already exists.");
        }
        boolean titleExists = serviceRepository.existsByTitleIgnoreCaseAndIdNot(request.getTitle(), id);

        if (titleExists) {
            throw new ConflictException("title", "A service with this title already exists.");
        }

        service.setCode(request.getCode());
        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());

        ProviderEntity provider =
                providerRepository.findById(request.getProviderId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Provider",
                                        "id",
                                        request.getProviderId()
                                ));

        service.setProvider(provider);

        return serviceMapper.toDTO(serviceRepository.save(service));
    }
}
