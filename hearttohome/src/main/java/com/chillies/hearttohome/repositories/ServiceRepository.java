package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.models.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findAllByOrderByCodeAsc();

    Page<ServiceEntity> findByIsEnabledTrue(Pageable pageable);

    Page<ServiceEntity> findByIsEnabledTrueAndTitleContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

    List<ServiceEntity> findByIdInAndIsEnabledTrue(List<Long> ids);

    List<ServiceEntity> findByIsEnabledFalseOrderByCodeAsc();

    List<ServiceEntity> findByIsEnabledTrueOrderByCodeAsc();

    boolean existsByTitleIgnoreCase(String title);
    boolean existsByCode(String code);

}