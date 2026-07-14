package com.chillies.hearttohome.services;


import com.chillies.hearttohome.DTO.ServiceDTO;
import com.chillies.hearttohome.models.ProviderEntity;
import com.chillies.hearttohome.models.ServiceEntity;
import com.chillies.hearttohome.repositories.ProviderRepository;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.util.EmailService;
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
    private final EmailService emailService;

    @Override
    public Page<ServiceEntity> getServices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return serviceRepository.findAll(pageable);
    }

    @Override
    public ResponseEntity<ServiceEntity> addService(ServiceDTO serviceDTO) {

        String title = serviceDTO.getTitle().trim();

        StringBuilder codeBuilder = new StringBuilder("HS_");

        String[] words = title.split("\\s+");

        if (words.length == 1) {
            codeBuilder.append(
                    Character.toUpperCase(words[0].charAt(0))
            );
        } else {
            for (String word : words) {
                codeBuilder.append(
                        Character.toUpperCase(word.charAt(0))
                );
            }
        }

        String code = codeBuilder.toString();


        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setCode(code);
        serviceEntity.setEnabled(serviceDTO.isEnabled());
        serviceEntity.setDescription(serviceDTO.getDescription());
        serviceEntity.setPrice(serviceDTO.getPrice());
        serviceEntity.setTitle(serviceDTO.getTitle());

        ProviderEntity provider = providerRepository.findById(serviceDTO.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        serviceEntity.setProvider(provider);
        ServiceEntity saved = serviceRepository.save(serviceEntity);

        return ResponseEntity.ok(saved);
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
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setEnabled(false);

        serviceRepository.save(service);
    }
    @Override
    public List<ServiceEntity> getDisabledServices() {
        return serviceRepository.findByIsEnabledFalseOrderByCodeAsc();
    }

    @Override
    @Transactional
    public void enableService(Long id) {

        ServiceEntity service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setEnabled(true);

        serviceRepository.save(service);
    }
}
