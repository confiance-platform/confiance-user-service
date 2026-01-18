package com.confiance.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralSummary {

    private Long userId;
    private String referralCode;
    private Long totalReferrals;
    private BigDecimal totalCommissionEarned;
    private BigDecimal pendingCommission;
    private List<ReferralResponse> recentReferrals;
}
