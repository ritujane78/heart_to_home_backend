package com.chillies.hearttohome.DTO;

import java.util.List;

public record ServiceValidationResponse(
        boolean valid,
        String message,
        List<ServiceDTOResponse> services
) {}