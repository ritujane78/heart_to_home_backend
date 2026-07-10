package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.models.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, String> {
    List<ServiceEntity> findAllByOrderByCodeAsc();
    Page<ServiceEntity> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

}
