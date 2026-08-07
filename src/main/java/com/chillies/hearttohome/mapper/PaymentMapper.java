package com.chillies.hearttohome.mapper;

import com.chillies.hearttohome.DTO.PaymentInfoDTO;
import com.chillies.hearttohome.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    Payment toEntity(PaymentInfoDTO request);

    PaymentInfoDTO toDTO(Payment payment);

}