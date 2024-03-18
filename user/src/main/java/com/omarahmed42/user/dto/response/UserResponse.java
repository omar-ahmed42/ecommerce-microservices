package com.omarahmed42.user.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(UUID id, String firstName, String lastName, LocalDate dateOfBirth, String email, String phoneNumber) {

}
