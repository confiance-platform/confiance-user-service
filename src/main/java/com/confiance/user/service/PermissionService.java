package com.confiance.user.service;

import com.confiance.common.dto.UserPermissionsResponse;
import com.confiance.common.enums.Permission;
import com.confiance.common.exception.BadRequestException;
import com.confiance.common.exception.ResourceNotFoundException;
import com.confiance.user.entity.User;
import com.confiance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service for managing user permissions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final UserRepository userRepository;

    /**
     * Get user permissions
     */
    public UserPermissionsResponse getUserPermissions(Long userId) {
        log.info("Getting permissions for user: {}", userId);
        User user = findUserById(userId);

        return UserPermissionsResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .permissions(user.getPermissions())
                .build();
    }

    /**
     * Grant permissions to a user (adds to existing permissions)
     */
    @Transactional
    public UserPermissionsResponse grantPermissions(Long userId, Set<Permission> permissions) {
        log.info("Granting permissions {} to user: {}", permissions, userId);

        if (permissions == null || permissions.isEmpty()) {
            throw new BadRequestException("Permissions cannot be empty");
        }

        User user = findUserById(userId);
        user.getPermissions().addAll(permissions);
        User updatedUser = userRepository.save(user);

        log.info("Successfully granted {} permissions to user: {}", permissions.size(), userId);
        return toPermissionsResponse(updatedUser);
    }

    /**
     * Revoke permissions from a user (removes from existing permissions)
     */
    @Transactional
    public UserPermissionsResponse revokePermissions(Long userId, Set<Permission> permissions) {
        log.info("Revoking permissions {} from user: {}", permissions, userId);

        if (permissions == null || permissions.isEmpty()) {
            throw new BadRequestException("Permissions cannot be empty");
        }

        User user = findUserById(userId);
        user.getPermissions().removeAll(permissions);
        User updatedUser = userRepository.save(user);

        log.info("Successfully revoked {} permissions from user: {}", permissions.size(), userId);
        return toPermissionsResponse(updatedUser);
    }

    /**
     * Set user permissions (replaces all existing permissions)
     */
    @Transactional
    public UserPermissionsResponse setPermissions(Long userId, Set<Permission> permissions) {
        log.info("Setting permissions {} for user: {}", permissions, userId);

        if (permissions == null) {
            throw new BadRequestException("Permissions cannot be null");
        }

        User user = findUserById(userId);
        user.getPermissions().clear();
        user.getPermissions().addAll(permissions);
        User updatedUser = userRepository.save(user);

        log.info("Successfully set {} permissions for user: {}", permissions.size(), userId);
        return toPermissionsResponse(updatedUser);
    }

    /**
     * Check if user has a specific permission
     */
    public boolean hasPermission(Long userId, Permission permission) {
        User user = findUserById(userId);
        return user.getPermissions().contains(permission);
    }

    /**
     * Check if user has all of the specified permissions
     */
    public boolean hasAllPermissions(Long userId, Set<Permission> permissions) {
        User user = findUserById(userId);
        return user.getPermissions().containsAll(permissions);
    }

    /**
     * Check if user has any of the specified permissions
     */
    public boolean hasAnyPermission(Long userId, Set<Permission> permissions) {
        User user = findUserById(userId);
        for (Permission permission : permissions) {
            if (user.getPermissions().contains(permission)) {
                return true;
            }
        }
        return false;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private UserPermissionsResponse toPermissionsResponse(User user) {
        return UserPermissionsResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .permissions(user.getPermissions())
                .build();
    }
}