package com.confiance.user.service;

import com.confiance.common.dto.LoginRequest;
import com.confiance.common.dto.UserInfo;
import com.confiance.common.dto.PageResponse;
import com.confiance.common.enums.UserRole;
import com.confiance.common.enums.UserStatus;
import com.confiance.common.exception.BadRequestException;
import com.confiance.common.exception.ConflictException;
import com.confiance.common.exception.ResourceNotFoundException;
import com.confiance.user.dto.UserRegistrationRequest;
import com.confiance.user.dto.UserResponse;
import com.confiance.user.dto.UserUpdateRequest;
import com.confiance.user.entity.User;
import com.confiance.user.mapper.UserMapper;
import com.confiance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("User with email " + request.getEmail() + " already exists");
        }

        if (userRepository.existsByContactNumber(request.getContactNumber())) {
            throw new ConflictException("User with contact number " + request.getContactNumber() + " already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        sendWelcomeEmail(savedUser);

        return userMapper.toResponse(savedUser);
    }

    public UserResponse getUserById(Long userId) {
        User user = findUserById(userId);
        return userMapper.toResponse(user);
    }

    public UserInfo getUserInfo(Long userId) {
        User user = findUserById(userId);
        return userMapper.toUserInfo(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = findUserById(userId);
        userMapper.updateEntity(user, request);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    public PageResponse<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userRepository.findAll(pageable);
        return buildPageResponse(userPage);
    }

    public UserInfo validateCredentials(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return userMapper.toUserInfo(user);
    }

    @Transactional
    public UserResponse addRoleToUser(Long userId, UserRole role) {
        User user = findUserById(userId);
        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public UserResponse removeRoleFromUser(Long userId, UserRole role) {
        User user = findUserById(userId);
        user.getRoles().remove(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private PageResponse<UserResponse> buildPageResponse(Page<User> userPage) {
        return PageResponse.<UserResponse>builder()
                .content(userPage.getContent().stream()
                        .map(userMapper::toResponse)
                        .toList())
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .first(userPage.isFirst())
                .empty(userPage.isEmpty())
                .build();
    }

    private void sendWelcomeEmail(User user) {
        try {
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("to", user.getEmail());
            emailData.put("subject", "Welcome to Confiance Financial Platform");
            emailData.put("body", String.format("Dear %s %s,\n\nWelcome to Confiance! Your account has been created successfully.",
                    user.getFirstName(), user.getLastName()));

            restTemplate.postForObject(notificationServiceUrl + "/send-email", emailData, Void.class);
        } catch (Exception e) {
            log.error("Failed to send welcome email: {}", e.getMessage());
        }
    }
}