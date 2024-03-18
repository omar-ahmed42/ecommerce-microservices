package com.omarahmed42.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.omarahmed42.user.dto.request.UserRegistration;
import com.omarahmed42.user.dto.response.UserResponse;
import com.omarahmed42.user.model.User;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toEntity(UserRegistration userRegistration);

    UserResponse toUserResponse(User user);
}
