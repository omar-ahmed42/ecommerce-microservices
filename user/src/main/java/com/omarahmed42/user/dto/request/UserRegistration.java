package com.omarahmed42.user.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.omarahmed42.user.validation.annotation.ValidAdult;
import com.omarahmed42.user.validation.annotation.ValidName;
import com.omarahmed42.user.validation.annotation.ValidPassword;
import com.omarahmed42.user.validation.annotation.ValidPhoneNumber;

import jakarta.validation.constraints.Email;

public record UserRegistration(
                @ValidName(fieldName = "First Name") String firstName,
                @ValidName(fieldName = "Last Name") String lastName,
                @ValidAdult @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateOfBirth,
                @Email String email,
                @ValidPassword String password,
                @ValidPhoneNumber String phoneNumber) {

}
