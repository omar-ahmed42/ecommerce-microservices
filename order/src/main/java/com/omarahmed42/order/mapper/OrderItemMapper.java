package com.omarahmed42.order.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.omarahmed42.order.model.OrderItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderItemMapper {
    OrderItem toEntity(com.omarahmed42.order.dto.message.domain.OrderItem orderItem);

    @InheritInverseConfiguration
    com.omarahmed42.order.dto.message.domain.OrderItem toOrderItem(OrderItem orderItem);
    
    List<com.omarahmed42.order.dto.message.domain.OrderItem> toOrderItemList(List<OrderItem> orderItem);
}
