package com.omarahmed42.user.service;

import java.util.UUID;

import org.springframework.validation.annotation.Validated;

import com.omarahmed42.user.dto.request.UserRegistration;
import com.omarahmed42.user.dto.request.UserUpdate;
import com.omarahmed42.user.dto.response.UserResponse;

import jakarta.validation.Valid;

@Validated
public interface UserService {
    UserResponse register(@Valid UserRegistration user);

    UserResponse updateUser(UUID id, UserUpdate user);

    void deleteUser(UUID id);

    UserResponse getUser(UUID id);
}
