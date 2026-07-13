package com.chillies.hearttohome.services;

import com.chillies.hearttohome.DTO.ServiceDTO;
import com.chillies.hearttohome.models.ServiceEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Services {

    Page<ServiceEntity> getServices(int page, int size);

    ResponseEntity<ServiceEntity> addService(ServiceDTO serviceDTO);

    @Transactional
    void deleteService(Long id);

    List<ServiceEntity> getDisabledServices();

    void enableService(Long id);

}
