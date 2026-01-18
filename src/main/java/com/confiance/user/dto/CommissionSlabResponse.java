package com.confiance.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionSlabResponse {

    private Long id;
    private String name;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal commissionPercentage;
    private String description;
    private boolean active;
    private Integer applicableQuarter;
    private Integer applicableYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
