package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.models.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, String> {
    List<ServiceEntity> findAllByOrderByCodeAsc();

}
