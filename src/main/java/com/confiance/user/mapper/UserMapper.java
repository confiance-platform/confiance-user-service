package com.confiance.user.mapper;

import com.confiance.common.dto.UserInfo;
import com.confiance.user.dto.UserRegistrationRequest;
import com.confiance.user.dto.UserResponse;
import com.confiance.user.dto.UserUpdateRequest;
import com.confiance.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRegistrationRequest request) {
        return User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .contactNumber(request.getContactNumber())
                .country(request.getCountry())
                .state(request.getState())
                .city(request.getCity())
                .address(request.getAddress())
                .postalCode(request.getPostalCode())
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .contactNumber(user.getContactNumber())
                .country(user.getCountry())
                .state(user.getState())
                .city(user.getCity())
                .address(user.getAddress())
                .postalCode(user.getPostalCode())
                .roles(user.getRoles())
                .permissions(user.getPermissions())
                .status(user.getStatus())
                .emailVerified(user.isEmailVerified())
                .phoneVerified(user.isPhoneVerified())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    public UserInfo toUserInfo(User user) {
        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .permissions(user.getPermissions())
                .build();
    }

    public void updateEntity(User user, UserUpdateRequest request) {
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getContactNumber() != null) {
            user.setContactNumber(request.getContactNumber());
        }
        if (request.getCountry() != null) {
            user.setCountry(request.getCountry());
        }
        if (request.getState() != null) {
            user.setState(request.getState());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getPostalCode() != null) {
            user.setPostalCode(request.getPostalCode());
        }
    }
}