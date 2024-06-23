package com.omarahmed42.order.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.omarahmed42.order.mapper.OrderItemMapper;
import com.omarahmed42.order.model.OrderItem;
import com.omarahmed42.order.repository.OrderItemRepository;
import com.omarahmed42.order.service.OrderItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<com.omarahmed42.order.dto.message.domain.OrderItem> getOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_id(orderId);
        return orderItemMapper.toOrderItemList(orderItems);
    }
}
