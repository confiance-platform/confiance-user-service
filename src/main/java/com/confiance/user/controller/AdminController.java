package com.confiance.user.controller;

import com.confiance.common.constants.ApiConstants;
import com.confiance.common.dto.ApiResponse;
import com.confiance.common.dto.PermissionRequest;
import com.confiance.common.dto.UserPermissionsResponse;
import com.confiance.common.enums.Permission;
import com.confiance.user.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Controller for admin operations including permission management
 */
@RestController
@RequestMapping(ApiConstants.ADMIN_BASE)
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final PermissionService permissionService;

    /**
     * Get all available permissions
     * GET /api/v1/admin/permissions/available
     */
    @GetMapping("/permissions/available")
    public ResponseEntity<ApiResponse<Set<Permission>>> getAvailablePermissions() {
        log.info("Getting all available permissions");
        Set<Permission> permissions = Permission.getAllPermissions();

        return ResponseEntity.ok(
                ApiResponse.<Set<Permission>>builder()
                        .success(true)
                        .message("Available permissions retrieved successfully")
                        .data(permissions)
                        .build()
        );
    }

    /**
     * Get user permissions by user ID
     * GET /api/v1/admin/permissions/user/{userId}
     */
    @GetMapping("/permissions/user/{userId}")
    public ResponseEntity<ApiResponse<UserPermissionsResponse>> getUserPermissions(
            @PathVariable Long userId) {
        log.info("Getting permissions for user: {}", userId);
        UserPermissionsResponse response = permissionService.getUserPermissions(userId);

        return ResponseEntity.ok(
                ApiResponse.<UserPermissionsResponse>builder()
                        .success(true)
                        .message("User permissions retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    /**
     * Grant permissions to a user
     * POST /api/v1/admin/permissions/grant
     */
    @PostMapping("/permissions/grant")
    public ResponseEntity<ApiResponse<UserPermissionsResponse>> grantPermissions(
            @Valid @RequestBody PermissionRequest request) {
        log.info("Granting permissions to user: {}", request.getUserId());
        UserPermissionsResponse response = permissionService.grantPermissions(
                request.getUserId(),
                request.getPermissions()
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<UserPermissionsResponse>builder()
                        .success(true)
                        .message("Permissions granted successfully")
                        .data(response)
                        .build()
        );
    }

    /**
     * Revoke permissions from a user
     * POST /api/v1/admin/permissions/revoke
     */
    @PostMapping("/permissions/revoke")
    public ResponseEntity<ApiResponse<UserPermissionsResponse>> revokePermissions(
            @Valid @RequestBody PermissionRequest request) {
        log.info("Revoking permissions from user: {}", request.getUserId());
        UserPermissionsResponse response = permissionService.revokePermissions(
                request.getUserId(),
                request.getPermissions()
        );

        return ResponseEntity.ok(
                ApiResponse.<UserPermissionsResponse>builder()
                        .success(true)
                        .message("Permissions revoked successfully")
                        .data(response)
                        .build()
        );
    }

    /**
     * Set user permissions (replaces all existing permissions)
     * PUT /api/v1/admin/permissions/user/{userId}
     */
    @PutMapping("/permissions/user/{userId}")
    public ResponseEntity<ApiResponse<UserPermissionsResponse>> setUserPermissions(
            @PathVariable Long userId,
            @RequestBody Set<Permission> permissions) {
        log.info("Setting permissions for user: {}", userId);
        UserPermissionsResponse response = permissionService.setPermissions(userId, permissions);

        return ResponseEntity.ok(
                ApiResponse.<UserPermissionsResponse>builder()
                        .success(true)
                        .message("User permissions updated successfully")
                        .data(response)
                        .build()
        );
    }

    /**
     * Check if user has a specific permission
     * GET /api/v1/admin/permissions/user/{userId}/has/{permission}
     */
    @GetMapping("/permissions/user/{userId}/has/{permission}")
    public ResponseEntity<ApiResponse<Boolean>> hasPermission(
            @PathVariable Long userId,
            @PathVariable Permission permission) {
        log.info("Checking if user {} has permission: {}", userId, permission);
        boolean hasPermission = permissionService.hasPermission(userId, permission);

        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder()
                        .success(true)
                        .message("Permission check completed")
                        .data(hasPermission)
                        .build()
        );
    }
}