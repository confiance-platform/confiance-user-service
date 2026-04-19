package com.confiance.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStats {
    private long totalUsers;
    private long activeUsers;
    private long suspendedUsers;
    private long newUsersThisMonth;
    private BigDecimal totalAUM;
    private long usersWithInvestments;
    private long totalTrades;
    private long openTrades;
}
