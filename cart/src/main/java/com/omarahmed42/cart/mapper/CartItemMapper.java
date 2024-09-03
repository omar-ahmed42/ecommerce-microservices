package com.omarahmed42.cart.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.omarahmed42.cart.dto.request.CartItemCreation;
import com.omarahmed42.cart.dto.request.CartItemUpdate;
import com.omarahmed42.cart.dto.response.CartItemResponse;
import com.omarahmed42.cart.model.CartItem;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartItemMapper {
    CartItem toEntity(CartItemCreation cartItemCreation);

    Collection<CartItem> fromCategoryCategories(Collection<CartItemCreation> cartItemCreation);

    CartItem toEntity(CartItemResponse cartItemResponse);

    Collection<CartItem> fromCartItemResponses(Collection<CartItemResponse> cartItemResponse);

    @InheritInverseConfiguration(name = "toEntity")
    CartItemCreation toCartItemCreation(CartItem cartItem);

    @InheritInverseConfiguration
    CartItemResponse toCartItemResponse(CartItem cartItem);

    void toTargetEntity(@MappingTarget CartItem cartItem, CartItemUpdate cartItemUpdate);

    List<CartItemResponse> toCartItemResponses(Collection<CartItem> cartItems);

}
