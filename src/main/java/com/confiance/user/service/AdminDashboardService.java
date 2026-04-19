package com.confiance.user.service;

import com.confiance.common.dto.ApiResponse;
import com.confiance.common.dto.PageResponse;
import com.confiance.common.enums.UserStatus;
import com.confiance.user.dto.AdminDashboardStats;
import com.confiance.user.dto.UserWithInvestmentSummary;
import com.confiance.user.entity.User;
import com.confiance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${portfolio.service.url:http://localhost:8086}")
    private String portfolioServiceUrl;

    public AdminDashboardStats getDashboardStats() {
        log.info("Fetching admin dashboard stats");

        // User statistics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(UserStatus.ACTIVE);
        long suspendedUsers = userRepository.countByStatus(UserStatus.SUSPENDED);

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long newUsersThisMonth = userRepository.countNewUsersThisMonth(startOfMonth);

        // Portfolio/Trade statistics from portfolio-service
        BigDecimal totalAUM = BigDecimal.ZERO;
        long usersWithInvestments = 0;
        long totalTrades = 0;
        long openTrades = 0;

        try {
            ResponseEntity<ApiResponse<Map<String, Object>>> response = restTemplate.exchange(
                    portfolioServiceUrl + "/api/v1/admin/portfolio/stats",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<Map<String, Object>>>() {}
            );

            if (response.getBody() != null && response.getBody().isSuccess() && response.getBody().getData() != null) {
                Map<String, Object> portfolioStats = response.getBody().getData();
                totalAUM = new BigDecimal(String.valueOf(portfolioStats.getOrDefault("totalAUM", "0")));
                usersWithInvestments = ((Number) portfolioStats.getOrDefault("usersWithInvestments", 0L)).longValue();
                totalTrades = ((Number) portfolioStats.getOrDefault("totalTrades", 0L)).longValue();
                openTrades = ((Number) portfolioStats.getOrDefault("openTrades", 0L)).longValue();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch portfolio stats from portfolio-service: {}", e.getMessage());
        }

        return AdminDashboardStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .suspendedUsers(suspendedUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .totalAUM(totalAUM)
                .usersWithInvestments(usersWithInvestments)
                .totalTrades(totalTrades)
                .openTrades(openTrades)
                .build();
    }

    public PageResponse<UserWithInvestmentSummary> getUsersWithInvestments(
            int page, int size, String sortBy, String sortDirection,
            UserStatus status, Boolean hasInvestments,
            BigDecimal minInvestment, BigDecimal maxInvestment) {

        log.info("Fetching users with investment summaries");

        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage;
        if (status != null) {
            userPage = userRepository.findByStatus(status, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserWithInvestmentSummary> usersWithSummary = userPage.getContent().stream()
                .map(user -> {
                    UserWithInvestmentSummary.InvestmentSummary investmentSummary = fetchInvestmentSummary(user.getId());

                    // Apply investment filters if provided
                    if (hasInvestments != null) {
                        boolean userHasInvestments = investmentSummary.getTotalInvested().compareTo(BigDecimal.ZERO) > 0;
                        if (hasInvestments != userHasInvestments) {
                            return null;
                        }
                    }
                    if (minInvestment != null && investmentSummary.getTotalInvested().compareTo(minInvestment) < 0) {
                        return null;
                    }
                    if (maxInvestment != null && investmentSummary.getTotalInvested().compareTo(maxInvestment) > 0) {
                        return null;
                    }

                    return UserWithInvestmentSummary.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .phone(user.getContactNumber())
                            .status(user.getStatus())
                            .roles(user.getRoles())
                            .createdAt(user.getCreatedAt())
                            .investmentSummary(investmentSummary)
                            .build();
                })
                .filter(user -> user != null)
                .toList();

        return PageResponse.<UserWithInvestmentSummary>builder()
                .content(usersWithSummary)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .first(userPage.isFirst())
                .empty(usersWithSummary.isEmpty())
                .build();
    }

    private UserWithInvestmentSummary.InvestmentSummary fetchInvestmentSummary(Long userId) {
        try {
            ResponseEntity<ApiResponse<Map<String, Object>>> response = restTemplate.exchange(
                    portfolioServiceUrl + "/api/v1/trades/user/" + userId + "/summary",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<Map<String, Object>>>() {}
            );

            if (response.getBody() != null && response.getBody().isSuccess() && response.getBody().getData() != null) {
                Map<String, Object> summary = response.getBody().getData();
                return UserWithInvestmentSummary.InvestmentSummary.builder()
                        .totalInvested(new BigDecimal(String.valueOf(summary.getOrDefault("totalInvested", "0"))))
                        .currentValue(new BigDecimal(String.valueOf(summary.getOrDefault("currentValue", "0"))))
                        .totalReturns(new BigDecimal(String.valueOf(summary.getOrDefault("totalPL", "0"))))
                        .totalTrades(((Number) summary.getOrDefault("totalTrades", 0)).intValue())
                        .openTrades(((Number) summary.getOrDefault("openTrades", 0)).intValue())
                        .build();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch investment summary for user {}: {}", userId, e.getMessage());
        }

        return UserWithInvestmentSummary.InvestmentSummary.builder()
                .totalInvested(BigDecimal.ZERO)
                .currentValue(BigDecimal.ZERO)
                .totalReturns(BigDecimal.ZERO)
                .totalTrades(0)
                .openTrades(0)
                .build();
    }
}
