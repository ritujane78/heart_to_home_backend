package com.chillies.hearttohome.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderServiceResponse {

    private Long id;
    private String code;
    private String title;
    private String description;
    private String price;
    private String providerName;
}