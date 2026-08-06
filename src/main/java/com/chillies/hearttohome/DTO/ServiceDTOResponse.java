package com.chillies.hearttohome.DTO;

import lombok.Data;

@Data
public class ServiceDTOResponse {
    private Long id;

    private Long providerId;

    private String title;

    private String description;

    private Double price;

    private String code;

    private boolean isEnabled;
}
