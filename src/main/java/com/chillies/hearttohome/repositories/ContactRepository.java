package com.chillies.hearttohome.repositories;


import com.chillies.hearttohome.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
  @Query(name = "Contact.findByStatus")
  List<Contact> fetchByStatus(String status);

  @Query(name = "Contact.findByStatusWithNativeQuery")
  List<Contact> fetchByStatusWithNativeQuery(String status);
  }
