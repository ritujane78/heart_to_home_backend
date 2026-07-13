package com.chillies.hearttohome.controllers;

import com.chillies.hearttohome.models.ProviderEntity;
import com.chillies.hearttohome.services.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping
    public List<ProviderEntity> getProviders() {
        return providerService.getProviders();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addProvider(
            @RequestBody ProviderEntity provider) {

        try {
            return ResponseEntity.ok(
                    providerService.addProvider(provider));

        } catch (IllegalArgumentException ex) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }
}
