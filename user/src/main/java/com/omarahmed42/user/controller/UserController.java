package com.omarahmed42.user.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.omarahmed42.user.dto.request.UserRegistration;
import com.omarahmed42.user.dto.response.UserResponse;
import com.omarahmed42.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> register(@RequestBody @Validated UserRegistration user) {
        UserResponse response = userService.register(user);
        return ResponseEntity.created(URI.create(response.id().toString())).body(response);
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping(value = "/users/me/avatar")
    public ResponseEntity<Long> uploadAvatar(MultipartFile avatar) {
        return ResponseEntity.ok(userService.saveAvatar(avatar));
    }

}
