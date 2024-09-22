package com.omarahmed42.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressCreation(
        @NotNull(message = "City cannot be empty") @NotBlank(message = "City cannot be empty") String city,
        @NotNull(message = "Country cannot be empty") @NotBlank(message = "Country cannot be empty") String country,
        @NotNull(message = "Address line cannot be empty") @NotBlank(message = "Address line cannot be empty") String addressLine,
        @NotNull(message = "Zipcode cannot be empty") @NotBlank(message = "Zipcode cannot be empty") String zipCode) {

}
