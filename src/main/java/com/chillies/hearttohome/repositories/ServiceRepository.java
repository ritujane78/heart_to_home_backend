package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    Page<ServiceEntity> findByIsEnabledTrue(Pageable pageable);

    Page<ServiceEntity> findByIsEnabledTrueAndTitleContainingIgnoreCase(
            String keyword,
            Pageable pageable
    );

    List<ServiceEntity> findAllByIdInAndIsEnabledTrue(List<Long> ids);

    List<ServiceEntity> findByIsEnabledFalseOrderByCodeAsc();

    boolean existsByTitleIgnoreCase(String title);

    boolean existsByCode(String code);

    List<ServiceEntity> findByIsEnabledTrue();

    boolean existsByCodeAndIdNot(String code, Long id);

    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);

}