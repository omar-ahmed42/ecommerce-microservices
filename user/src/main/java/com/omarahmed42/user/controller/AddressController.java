package com.omarahmed42.user.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.omarahmed42.user.dto.request.AddressCreation;
import com.omarahmed42.user.dto.response.AddressResponse;
import com.omarahmed42.user.service.AddressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<AddressResponse> addAddress(@RequestBody @Validated AddressCreation address) {
        AddressResponse res = addressService.addAddress(address);
        Long addressId = res.id();
        return ResponseEntity.created(URI.create(addressId.toString())).body(res);
    }

    @GetMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> getAddress(@PathVariable("id") Long id) {
        return ResponseEntity.ok(addressService.getAddress(id));
    }

    @GetMapping("/users/{id}/addresses")
    public ResponseEntity<java.util.List<AddressResponse>> getAddresses(@PathVariable("id") UUID userId) {
        return ResponseEntity.ok(addressService.getAddressesByUserId(userId));
    }
}
