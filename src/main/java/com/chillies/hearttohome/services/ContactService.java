package com.chillies.hearttohome.services;


import com.chillies.hearttohome.DTO.ContactRequestDto;
import com.chillies.hearttohome.DTO.ContactResponseDto;

import java.util.List;

public interface ContactService {

    boolean saveContact(ContactRequestDto contactRequestDto);
    List<ContactResponseDto> getAllOpenMessages();

    void updateMessageStatus(Long contactId, String status);
}
