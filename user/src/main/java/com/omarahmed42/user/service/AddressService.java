package com.omarahmed42.user.service;

import java.util.UUID;

import com.omarahmed42.user.dto.request.AddressCreation;
import com.omarahmed42.user.dto.response.AddressResponse;

import jakarta.validation.Valid;

public interface AddressService {
    Long addAddress(@Valid AddressCreation address);

    AddressResponse getAddress(Long id);

    Object getAddressesByUserId(UUID userId);
}
