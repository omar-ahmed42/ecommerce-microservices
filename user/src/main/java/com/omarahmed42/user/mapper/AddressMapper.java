package com.omarahmed42.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.omarahmed42.user.dto.request.AddressCreation;
import com.omarahmed42.user.dto.response.AddressResponse;
import com.omarahmed42.user.model.Address;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {
    Address toEntity(AddressCreation addressCreation);

    @Mapping(target = "userId", source = "user.id")
    AddressResponse toAddressResponse(Address address);

    java.util.List<AddressResponse> toAddressResponseList(java.util.List<Address> addresses);
}
