package com.chillies.hearttohome.DTO;

import com.chillies.hearttohome.models.ServiceEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class ServicePageResponse {

    private Page<ServiceEntity> services;
    private List<String> providerNames;

    public ServicePageResponse(Page<ServiceEntity> services, List<String> providerNames) {
        this.services = services;
        this.providerNames = providerNames;
    }

    // getters and setters
}
