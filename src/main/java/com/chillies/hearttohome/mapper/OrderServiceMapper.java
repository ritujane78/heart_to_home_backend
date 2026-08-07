package com.chillies.hearttohome.mapper;

import com.chillies.hearttohome.entity.GiftOrder;
import com.chillies.hearttohome.entity.OrderService;
import com.chillies.hearttohome.entity.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderServiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "giftOrder", source = "giftOrder")
    @Mapping(target = "service", source = "service")
    @Mapping(target = "code", source = "service.code")
    @Mapping(target = "title", source = "service.title")
    @Mapping(target = "description", source = "service.description")
    @Mapping(target = "price", source = "service.price")
    @Mapping(target = "providerName", source = "service.provider.name")
    OrderService toOrderService(ServiceEntity service, GiftOrder giftOrder);
}