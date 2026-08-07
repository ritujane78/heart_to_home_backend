package com.chillies.hearttohome.mapper;

import com.chillies.hearttohome.DTO.ProviderRequest;
import com.chillies.hearttohome.DTO.ProviderResponse;
import com.chillies.hearttohome.entity.ProviderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProviderMapper {

    ProviderResponse toResponse(ProviderEntity provider);

    List<ProviderResponse> toResponseList(List<ProviderEntity> providers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "services", ignore = true)
    ProviderEntity toEntity(ProviderRequest request);
}