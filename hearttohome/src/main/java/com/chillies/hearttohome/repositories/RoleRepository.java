package com.chillies.hearttohome.repositories;


import com.chillies.hearttohome.models.AppRole;
import com.chillies.hearttohome.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
