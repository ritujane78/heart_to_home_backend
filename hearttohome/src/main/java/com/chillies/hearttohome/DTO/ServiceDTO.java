package com.chillies.hearttohome.DTO;

import lombok.Data;

@Data
public class ServiceDTO {

    private String id;
    private String code;
    private String providerId;
    private String title;
    private String description;
    private Double price;

}
