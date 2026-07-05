package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {

}
