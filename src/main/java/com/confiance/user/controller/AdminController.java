package com.confiance.user.controller;

import com.confiance.common.constants.ApiConstants;
import com.confiance.common.dto.ApiResponse;
import com.confiance.common.dto.PageResponse;
import com.confiance.common.dto.PermissionRequest;
import com.confiance.common.dto.UserPermissionsResponse;
import com.confiance.common.enums.Permission;
import com.confiance.common.enums.UserStatus;
import com.confiance.user.dto.AdminDashboardStats;
import com.confiance.user.dto.UserWithInvestmentSummary;
import com.confiance.user.service.AdminDashboardService;
import com.confiance.user.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Controller for admin operations including permission management
 */
@RestController
@RequestMapping(ApiConstants.ADMIN_BASE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Admin Dashboard and Permission Management APIs")
public class AdminController {

    private final PermissionService permissionService;
    private final AdminDashboardService adminDashboardService;

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

    /**
     * Get admin dashboard statistics
     * GET /api/v1/admin/dashboard/stats
     */
    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get Dashboard Stats", description = "Get aggregated statistics for admin dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardStats>> getDashboardStats() {
        log.info("Fetching admin dashboard statistics");
        AdminDashboardStats stats = adminDashboardService.getDashboardStats();

        return ResponseEntity.ok(
                ApiResponse.<AdminDashboardStats>builder()
                        .success(true)
                        .message("Dashboard statistics retrieved successfully")
                        .data(stats)
                        .build()
        );
    }

    /**
     * Get users with investment summary
     * GET /api/v1/admin/users/with-investments
     */
    @GetMapping("/users/with-investments")
    @Operation(summary = "Get Users with Investments", description = "Get users list with their investment summary")
    public ResponseEntity<ApiResponse<PageResponse<UserWithInvestmentSummary>>> getUsersWithInvestments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) Boolean hasInvestments,
            @RequestParam(required = false) BigDecimal minInvestment,
            @RequestParam(required = false) BigDecimal maxInvestment) {

        log.info("Fetching users with investment summaries");
        PageResponse<UserWithInvestmentSummary> response = adminDashboardService.getUsersWithInvestments(
                page, size, sortBy, sortDirection, status, hasInvestments, minInvestment, maxInvestment);

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<UserWithInvestmentSummary>>builder()
                        .success(true)
                        .message("Users with investments retrieved successfully")
                        .data(response)
                        .build()
        );
    }
}