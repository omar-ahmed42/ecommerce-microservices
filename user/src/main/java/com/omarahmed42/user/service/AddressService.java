package com.omarahmed42.user.service;

import java.util.UUID;

import com.omarahmed42.user.dto.request.AddressCreation;
import com.omarahmed42.user.dto.response.AddressResponse;

import jakarta.validation.Valid;

public interface AddressService {
    AddressResponse addAddress(@Valid AddressCreation address);

    AddressResponse getAddress(Long id);

    java.util.List<AddressResponse> getAddressesByUserId(UUID userId);
}
