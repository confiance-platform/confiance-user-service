package com.confiance.user.repository;

import com.confiance.common.enums.UserRole;
import com.confiance.common.enums.UserStatus;
import com.confiance.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByContactNumber(String contactNumber);

    boolean existsByEmail(String email);

    boolean existsByContactNumber(String contactNumber);

    Page<User> findByStatus(UserStatus status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE :role MEMBER OF u.roles")
    Page<User> findByRole(UserRole role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.country = :country")
    Page<User> findByCountry(String country, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE :role MEMBER OF u.roles")
    long countByRole(UserRole role);
}