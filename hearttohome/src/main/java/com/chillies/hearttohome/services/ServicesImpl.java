package com.chillies.hearttohome.services;


import com.chillies.hearttohome.DTO.ServiceDTO;
import com.chillies.hearttohome.models.ServiceEntity;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.util.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;
    private final ServiceRepository serviceRepository;
    private final EmailService emailService;

    @Override
    public Page<ServiceEntity> getServices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return serviceRepository.findAll(pageable);
    }

    @Override
    public ResponseEntity<ServiceEntity> addService(ServiceDTO serviceDTO) {
        System.out.println("add service = " + serviceDTO);
        String code = serviceDTO.getCode().trim().toUpperCase();

        if (serviceRepository.existsByCodeIgnoreCase(code)) {
            throw new RuntimeException("A service with this code already exists.");
        }
        ServiceEntity serviceEntity = objectMapper.convertValue(serviceDTO, ServiceEntity.class);
        ServiceEntity saved = serviceRepository.save(serviceEntity);
        System.out.println(saved);

        return ResponseEntity.ok(saved);
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
