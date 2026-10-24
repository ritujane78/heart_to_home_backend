package com.chillies.hearttohome.DTO;

import java.time.Instant;

public record ContactResponseDto(Long contactId, String name, String email,
                                 String countryCode, String mobileNumber, String message, String status, Instant createdAt) {
}
