package com.confiance.user.dto;

import com.confiance.common.enums.Permission;
import com.confiance.common.enums.UserRole;
import com.confiance.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String country;
    private String state;
    private String city;
    private String address;
    private String postalCode;
    private Set<UserRole> roles;
    private Set<Permission> permissions;
    private UserStatus status;
    private boolean emailVerified;
    private boolean phoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}