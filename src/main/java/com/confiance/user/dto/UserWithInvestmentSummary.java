package com.confiance.user.dto;

import com.confiance.common.enums.UserRole;
import com.confiance.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithInvestmentSummary {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserStatus status;
    private Set<UserRole> roles;
    private LocalDateTime createdAt;
    private InvestmentSummary investmentSummary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvestmentSummary {
        private BigDecimal totalInvested;
        private BigDecimal currentValue;
        private BigDecimal totalReturns;
        private int totalTrades;
        private int openTrades;
    }
}
