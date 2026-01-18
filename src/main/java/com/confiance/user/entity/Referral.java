package com.confiance.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals", indexes = {
        @Index(name = "idx_referrer_user_id", columnList = "referrerUserId"),
        @Index(name = "idx_referred_user_id", columnList = "referredUserId"),
        @Index(name = "idx_referral_quarter", columnList = "quarter, year")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who made the referral (earns commission)
    @Column(nullable = false)
    private Long referrerUserId;

    // User who was referred (signed up using referral code)
    @Column(nullable = false)
    private Long referredUserId;

    // Name of referred person (for display)
    private String referredUserName;

    // Date when the referral was made (signup date)
    @Column(nullable = false)
    private LocalDate referralDate;

    // Quarter and Year for commission calculation
    @Column(nullable = false)
    private Integer quarter; // 1, 2, 3, or 4

    @Column(nullable = false)
    private Integer year;

    // Investment amount by referred user (for commission calculation)
    @Column(precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal referredUserInvestment = BigDecimal.ZERO;

    // Commission rate applied (snapshot at time of calculation)
    @Column(precision = 5, scale = 2)
    private BigDecimal commissionRate;

    // Commission amount earned
    @Column(precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal commissionEarned = BigDecimal.ZERO;

    // Status: PENDING (waiting for investment), ACTIVE (investment made), PAID (commission paid)
    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING";

    // Whether commission was paid out
    @Builder.Default
    private boolean paid = false;

    private LocalDate paidDate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Helper method to calculate quarter from date
    public static int getQuarterFromDate(LocalDate date) {
        int month = date.getMonthValue();
        return (month - 1) / 3 + 1;
    }
}
