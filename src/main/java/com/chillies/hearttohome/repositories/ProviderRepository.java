package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.models.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<ProviderEntity, Long> {

    List<ProviderEntity> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    Optional<ProviderEntity> findByNameIgnoreCase(String name);
}
