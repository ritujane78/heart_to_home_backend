package com.chillies.hearttohome.services;


import com.chillies.hearttohome.DTO.ServiceDTO;
import com.chillies.hearttohome.models.ServiceEntity;
import com.chillies.hearttohome.repositories.ServiceRepository;
import com.chillies.hearttohome.util.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicesImpl implements Services {

    private final ObjectMapper objectMapper;
    private final ServiceRepository serviceRepository;
    private final EmailService emailService;

    @Override
    public ResponseEntity<ServiceEntity> addService(ServiceDTO serviceDTO) {
        ServiceEntity serviceEntity = objectMapper.convertValue(serviceDTO, ServiceEntity.class);

        int next =(int) serviceRepository.count() + 1;


        serviceEntity.setCode("HS" + next);

        System.out.println("serviceEntity: " + serviceEntity);

        ServiceEntity saved = serviceRepository.save(serviceEntity);

        return ResponseEntity.ok(saved);
    }
    @Transactional
    @Override
    public void deleteService(String id) {

        serviceRepository.deleteById(id);

        List<ServiceEntity> services = serviceRepository.findAllByOrderByCodeAsc();

        int i = 1;

        for(ServiceEntity serviceItem : services){

            serviceItem.setCode("HS" + i);

            i++;
        }

        serviceRepository.saveAll(services);
    }
}
