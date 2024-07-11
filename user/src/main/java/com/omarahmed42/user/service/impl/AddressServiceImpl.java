package com.omarahmed42.user.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.omarahmed42.user.dto.request.AddressCreation;
import com.omarahmed42.user.dto.response.AddressResponse;
import com.omarahmed42.user.mapper.AddressMapper;
import com.omarahmed42.user.model.Address;
import com.omarahmed42.user.repository.AddressRepository;
import com.omarahmed42.user.repository.UserRepository;
import com.omarahmed42.user.service.AddressService;
import com.omarahmed42.user.utils.SecurityUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public Long addAddress(@Valid AddressCreation addressCreation) {
        String userId = SecurityUtils.getSubject();

        log.info("Address creation info: country {}, city {}, addressLine {}, zipCode {}", addressCreation.country(),
                addressCreation.city(), addressCreation.addressLine(), addressCreation.zipCode());
        Address address = addressMapper.toEntity(addressCreation);
        address.setUser(userRepository.getReferenceById(UUID.fromString(userId)));

        address = addressRepository.save(address);
        return address.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddress(@NotNull(message = "ID cannot be empty") Long id) {
        Address address = addressRepository.findById(id).orElseThrow();

        UUID authenticatedUserId = UUID.fromString(SecurityUtils.getSubject());
        if (address.getUser() != null && address.getUser().getId().equals(authenticatedUserId)) {
            throw new RuntimeException("Unauthorized");
        }
        return addressMapper.toAddressResponse(address);
    }

    @Override
    public Object getAddressesByUserId(UUID userId) {
        // TODO Auto-generated method stub
        return null;
    }

}
