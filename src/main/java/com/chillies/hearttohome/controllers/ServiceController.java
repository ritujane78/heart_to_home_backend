package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.DTO.ServiceDTO;
import com.chillies.hearttohome.models.ServiceEntity;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.services.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<ServiceEntity> getServices(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size);

        if (keyword.isBlank()) {
            return serviceRepository.findByIsEnabledTrue(pageable);
        }

        return serviceRepository.findByIsEnabledTrueAndTitleContainingIgnoreCase(
                keyword,
                pageable
        );
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/admin/update-service/{id}")
    public ResponseEntity<?> updateService(
            @PathVariable Long id,
            @RequestBody ServiceDTO request) {

        return ResponseEntity.ok(
                services.updateService(id, request)
        );
    }
}