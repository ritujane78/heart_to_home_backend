package com.chillies.hearttohome.repositories;


import com.chillies.hearttohome.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

  List<Contact> findByStatus(String status);

  }
