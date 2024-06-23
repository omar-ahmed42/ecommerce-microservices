package com.omarahmed42.order.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.omarahmed42.order.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItem toEntity(com.omarahmed42.order.dto.message.domain.OrderItem orderItem);

    com.omarahmed42.order.dto.message.domain.OrderItem toOrderItem(OrderItem orderItem);
    
    List<com.omarahmed42.order.dto.message.domain.OrderItem> toOrderItemList(List<OrderItem> orderItem);
}
