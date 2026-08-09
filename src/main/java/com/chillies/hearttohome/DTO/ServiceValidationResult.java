package com.chillies.hearttohome.DTO;

import com.chillies.hearttohome.entity.ServiceEntity;

import java.util.List;

public record ServiceValidationResult(
        boolean valid,
        String message,
        List<ServiceEntity> services
) {}