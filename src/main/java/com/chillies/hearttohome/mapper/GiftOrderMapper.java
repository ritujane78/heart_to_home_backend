package com.chillies.hearttohome.mapper;

import com.chillies.hearttohome.DTO.GiftOrderRequest;
import com.chillies.hearttohome.DTO.GiftOrderResponse;
import com.chillies.hearttohome.DTO.OrderServiceResponse;
import com.chillies.hearttohome.entity.GiftOrder;
import com.chillies.hearttohome.entity.OrderService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GiftOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    GiftOrder toEntity(GiftOrderRequest request);

    @Mapping(target = "emailSent", ignore = true)
    GiftOrderResponse toResponse(GiftOrder order);

    List<GiftOrderResponse> toResponse(List<GiftOrder> orders);

    @Mapping(target = "id", source = "service.id")
    @Mapping(target = "code", source = "service.code")
    @Mapping(target = "title", source = "service.title")
    @Mapping(target = "description", source = "service.description")
    @Mapping(target = "price", source = "service.price")
    @Mapping(target = "providerName", source = "service.provider.name")
    OrderServiceResponse toResponse(OrderService orderService);

    List<OrderServiceResponse> toOrderServiceResponses(List<OrderService> services);
}