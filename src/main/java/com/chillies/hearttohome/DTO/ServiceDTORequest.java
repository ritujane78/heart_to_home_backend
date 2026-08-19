package com.chillies.hearttohome.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceDTORequest {

    private Long providerId;

    private String title;

    private String description;

    private BigDecimal price;

    private String code;

    private boolean isEnabled;
}
