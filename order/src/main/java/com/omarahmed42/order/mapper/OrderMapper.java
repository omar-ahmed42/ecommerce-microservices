package com.omarahmed42.order.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.omarahmed42.order.dto.response.OrderDetails;
import com.omarahmed42.order.model.Order;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    Order toEntity(com.omarahmed42.order.dto.message.domain.Order order);

    @InheritInverseConfiguration
    com.omarahmed42.order.dto.message.domain.Order toOrder(Order order);

    Order toEntity(OrderDetails orderDetails);

    @InheritInverseConfiguration
    OrderDetails toOrderDetails(Order order);
}
