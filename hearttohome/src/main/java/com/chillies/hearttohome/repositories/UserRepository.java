package com.chillies.hearttohome.repositories;

import com.chillies.hearttohome.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
