package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.ServiceDTORequest;
import com.chillies.hearttohome.DTO.ServicePageResponse;
import com.chillies.hearttohome.entity.ServiceEntity;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.services.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceRepository serviceRepository;
    private final Services services;

    @GetMapping
    public ServicePageResponse getServices(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceEntity> services;

        if (keyword.isBlank()) {
            services = serviceRepository.findByIsEnabledTrue(pageable);
        } else {
            services = serviceRepository.findByIsEnabledTrueAndTitleContainingIgnoreCase(
                    keyword,
                    pageable
            );
        }

        // Get all matching services (without pagination)
        List<ServiceEntity> allMatchingServices = serviceRepository.findByIsEnabledTrue();

        List<String> providerNames = allMatchingServices.stream()
                .map(service -> service.getProvider().getName())
                .distinct()
                .toList();

        return new ServicePageResponse(services, providerNames);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin/update-service/{id}")
    public ResponseEntity<?> updateService(
            @PathVariable Long id,
            @RequestBody ServiceDTORequest request) {

        return ResponseEntity.ok(
                services.updateService(id, request)
        );
    }
}