package com.confiance.user.controller;

import com.confiance.common.dto.LoginRequest;
import com.confiance.common.dto.UserInfo;
import com.confiance.common.constants.ApiConstants;
import com.confiance.common.dto.ApiResponse;
import com.confiance.common.dto.PageResponse;
import com.confiance.common.enums.UserRole;
import com.confiance.user.dto.UserRegistrationRequest;
import com.confiance.user.dto.UserResponse;
import com.confiance.user.dto.UserUpdateRequest;
import com.confiance.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstants.USERS_BASE)
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User CRUD and Management APIs")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register User", description = "Register a new user")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User", description = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/info")
    @Operation(summary = "Get User Info", description = "Get user info for authentication")
    public ResponseEntity<ApiResponse<UserInfo>> getUserInfo(@PathVariable Long id) {
        UserInfo response = userService.getUserInfo(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update User", description = "Update user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete User", description = "Soft delete user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @GetMapping
    @Operation(summary = "Get All Users", description = "Get paginated list of users")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_SORT_BY) String sortBy,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_SORT_DIRECTION) String sortDirection) {
        PageResponse<UserResponse> response = userService.getAllUsers(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/validate-credentials")
    @Operation(summary = "Validate Credentials", description = "Validate user credentials for authentication")
    public ResponseEntity<ApiResponse<UserInfo>> validateCredentials(@Valid @RequestBody LoginRequest request) {
        UserInfo response = userService.validateCredentials(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/roles")
    @Operation(summary = "Add Role", description = "Add role to user")
    public ResponseEntity<ApiResponse<UserResponse>> addRole(
            @PathVariable Long id,
            @RequestParam UserRole role) {
        UserResponse response = userService.addRoleToUser(id, role);
        return ResponseEntity.ok(ApiResponse.success("Role added successfully", response));
    }

    @DeleteMapping("/{id}/roles")
    @Operation(summary = "Remove Role", description = "Remove role from user")
    public ResponseEntity<ApiResponse<UserResponse>> removeRole(
            @PathVariable Long id,
            @RequestParam UserRole role) {
        UserResponse response = userService.removeRoleFromUser(id, role);
        return ResponseEntity.ok(ApiResponse.success("Role removed successfully", response));
    }
}