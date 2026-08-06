package com.chillies.hearttohome.mapper;

import com.chillies.hearttohome.DTO.ServiceDTORequest;
import com.chillies.hearttohome.DTO.ServiceDTOResponse;
import com.chillies.hearttohome.entity.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(source = "provider.id", target = "providerId")
    ServiceDTOResponse toDTO(ServiceEntity entity);

    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "id", ignore = true)
    ServiceEntity toEntity(ServiceDTORequest dto);

    List<ServiceDTOResponse> toDTO(List<ServiceEntity> entities);

    List<ServiceEntity> toEntity(List<ServiceDTORequest> dtos);
}