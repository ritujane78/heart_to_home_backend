package com.chillies.hearttohome.DTO;

import com.chillies.hearttohome.entity.ServiceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ServicePageResponseDTO {

    private Page<ServiceEntity> services;

    private List<String> providerNames;

}