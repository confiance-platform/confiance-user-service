package com.confiance.user.config;

import com.confiance.common.enums.Permission;
import com.confiance.common.enums.UserRole;
import com.confiance.common.enums.UserStatus;
import com.confiance.user.entity.User;
import com.confiance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Loads default admin users on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultUsersLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String SUPER_ADMIN_EMAIL = "superadmin@confiance.com";
    private static final String ADMIN_EMAIL = "admin@confiance.com";
    private static final String DEFAULT_PASSWORD = "Admin@123";

    @Override
    public void run(String... args) {
        log.info("Checking and creating default admin users...");

        createSuperAdmin();
        createAdmin();

        log.info("Default admin users initialization completed");
    }

    private void createSuperAdmin() {
        if (userRepository.existsByEmail(SUPER_ADMIN_EMAIL)) {
            log.info("Super Admin user already exists: {}", SUPER_ADMIN_EMAIL);
            return;
        }

        log.info("Creating Super Admin user: {}", SUPER_ADMIN_EMAIL);

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_SUPER_ADMIN);

        User superAdmin = User.builder()
                .email(SUPER_ADMIN_EMAIL)
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .firstName("Super")
                .lastName("Admin")
                .contactNumber("+1234567890")
                .country("USA")
                .state("California")
                .city("San Francisco")
                .address("123 Admin Street")
                .postalCode("94102")
                .roles(roles)
                .permissions(Permission.getDefaultSuperAdminPermissions())
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .phoneVerified(true)
                .build();

        userRepository.save(superAdmin);
        log.info("Super Admin user created successfully: {}", SUPER_ADMIN_EMAIL);
        log.info("Super Admin credentials - Email: {}, Password: {}", SUPER_ADMIN_EMAIL, DEFAULT_PASSWORD);
    }

    private void createAdmin() {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            log.info("Admin user already exists: {}", ADMIN_EMAIL);
            return;
        }

        log.info("Creating Admin user: {}", ADMIN_EMAIL);

        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_ADMIN);

        User admin = User.builder()
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .firstName("Admin")
                .lastName("User")
                .contactNumber("+1234567891")
                .country("USA")
                .state("California")
                .city("San Francisco")
                .address("124 Admin Street")
                .postalCode("94102")
                .roles(roles)
                .permissions(Permission.getDefaultAdminPermissions())
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .phoneVerified(true)
                .build();

        userRepository.save(admin);
        log.info("Admin user created successfully: {}", ADMIN_EMAIL);
        log.info("Admin credentials - Email: {}, Password: {}", ADMIN_EMAIL, DEFAULT_PASSWORD);
    }
}