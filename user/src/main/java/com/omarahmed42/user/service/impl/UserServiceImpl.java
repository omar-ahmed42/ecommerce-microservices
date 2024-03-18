package com.omarahmed42.user.service.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.omarahmed42.user.dto.request.UserRegistration;
import com.omarahmed42.user.dto.request.UserUpdate;
import com.omarahmed42.user.dto.response.UserResponse;
import com.omarahmed42.user.exception.UserCreationException;
import com.omarahmed42.user.exception.UserNotFoundException;
import com.omarahmed42.user.mapper.UserMapper;
import com.omarahmed42.user.model.User;
import com.omarahmed42.user.repository.UserRepository;
import com.omarahmed42.user.service.UserService;

import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${keycloak.realm}")
    private String realm;

    private final UserRepository userRepository;
    private final Keycloak keycloak;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse register(@Valid UserRegistration userRegistration) {
        User user = userMapper.toEntity(userRegistration);
        user.setPassword(passwordEncoder.encode(userRegistration.password()));

        UserRepresentation userRepresentation = createUserRepresentationFrom(user, false, true);
        setPasswordRepresentation(userRepresentation, userRegistration.password());
        Response response = keycloak.realm(realm).users().create(userRepresentation);

        Object entity = response.getEntity();
        log.info("Entity type {}", entity.getClass().toString());
        log.info("Response |  Status: {} | Status Info: {}", response.getStatus(), response.getStatusInfo());

        if (!is2xx(response.getStatus())) {
            @SuppressWarnings("unchecked")
            Map<String, String> error = response.readEntity(Map.class);
            throw new UserCreationException(response.getStatus(), error.get("errorMessage"));
        }

        if (userRepresentation == null)
            throw new UserCreationException("Could not create a keycloak user");

        try {
            user.setId(UUID.fromString(extractUserId(response)));
            user = userRepository.save(user);
            log.info("Stored user in the database successfully with id {}", user.getId());
        } catch (Exception e) {
            log.error("Error while storing user to database {} || {}", e, e.getMessage());
            keycloak.realm(realm).users().delete(userRepresentation.getId());
            throw new UserCreationException("Failed to store user to database");
        }

        return userMapper.toUserResponse(user);
    }

    private String extractUserId(Response response) {
        return response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
    }

    private UserRepresentation createUserRepresentationFrom(User user, Boolean emailVerified, Boolean enabled) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(user.getId() == null ? null : user.getId().toString());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmailVerified(emailVerified);
        userRepresentation.setEnabled(enabled);
        return userRepresentation;
    }

    private void setPasswordRepresentation(UserRepresentation user, String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        credentialRepresentation.setTemporary(false);
        user.setCredentials(Arrays.asList(credentialRepresentation));
    }

    private boolean is2xx(int statusCode) {
        return statusCode >= 200 && statusCode <= 299;
    }

    @Override
    public UserResponse updateUser(UUID id, UserUpdate user) {
        return null;
    }
}
