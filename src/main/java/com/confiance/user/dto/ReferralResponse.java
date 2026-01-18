package com.confiance.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralResponse {

    private Long id;
    private Long referrerUserId;
    private Long referredUserId;
    private String referredUserName;
    private LocalDate referralDate;
    private Integer quarter;
    private Integer year;
    private BigDecimal referredUserInvestment;
    private BigDecimal commissionRate;
    private BigDecimal commissionEarned;
    private String status;
    private boolean paid;
    private LocalDate paidDate;
    private LocalDateTime createdAt;
}
