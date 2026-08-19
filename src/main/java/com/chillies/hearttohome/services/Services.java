package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.ServiceDTORequest;
import com.chillies.hearttohome.DTO.ServiceDTOResponse;
import com.chillies.hearttohome.DTO.ServicePageResponseDTO;
import com.chillies.hearttohome.entity.ServiceEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Services {

    ServicePageResponseDTO getServices(int page, int size);

    ResponseEntity<ServiceDTOResponse> addService(ServiceDTORequest serviceDTORequest);

    boolean titleExists(String title);

    @Transactional
    void deleteService(Long id);

    List<ServiceDTOResponse> getDisabledServices();

    void enableService(Long id);

    @Transactional
    ServiceDTOResponse updateService(
            Long id,
            ServiceDTORequest request);
}
