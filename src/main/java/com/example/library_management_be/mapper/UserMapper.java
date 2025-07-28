package com.example.library_management_be.mapper;

import com.example.library_management_be.dto.response.AdminUserResponse;
import com.example.library_management_be.dto.response.UserResponse;
import com.example.library_management_be.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toDto(User user);

    AdminUserResponse toAdminUserResponse(User user);
//    @Mapping(target = "role", expression = "java(ERole.valueOf(userRequest.getRole()))")
//    User toEntity(UserRequest userRequest);

}
