package com.omarahmed42.user.dto.response;

import java.util.UUID;

public record AddressResponse(Long id, String city, String country, String addressLine, String zipCode, UUID userId) {
}