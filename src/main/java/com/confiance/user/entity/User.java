package com.confiance.user.entity;

import com.confiance.common.enums.Permission;
import com.confiance.common.enums.Salutation;
import com.confiance.common.enums.UserRole;
import com.confiance.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_contact_number", columnList = "contactNumber"),
        @Index(name = "idx_referral_code", columnList = "referralCode")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Salutation salutation;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String contactNumber;

    @Column(nullable = false)
    private String country;

    // Unique referral code for this user (auto-generated)
    @Column(unique = true)
    private String referralCode;

    // The referral code used during registration (who referred this user)
    private String referredByCode;

    private String state;
    private String city;
    private String address;
    private String postalCode;

    private LocalDateTime dateOfBirth;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    private boolean emailVerified = false;
    private boolean phoneVerified = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    @PrePersist
    public void prePersist() {
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(UserRole.ROLE_USER);
        }

        // Initialize default permissions based on roles
        if (permissions == null || permissions.isEmpty()) {
            permissions = new HashSet<>();
            for (UserRole role : roles) {
                permissions.addAll(Permission.getPermissionsByRole(role));
            }
        }
    }
}