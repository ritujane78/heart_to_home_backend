package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.ServiceDTO;
import com.chillies.hearttohome.models.ServiceEntity;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

public interface Services {

    ResponseEntity<ServiceEntity> addService(ServiceDTO serviceDTO);

    @Transactional
    void deleteService(String id);
}
