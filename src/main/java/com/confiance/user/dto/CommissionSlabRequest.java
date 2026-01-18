package com.confiance.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionSlabRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Minimum amount is required")
    @Positive(message = "Minimum amount must be positive")
    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    @NotNull(message = "Commission percentage is required")
    @Positive(message = "Commission percentage must be positive")
    private BigDecimal commissionPercentage;

    private String description;

    private boolean active = true;

    private Integer applicableQuarter;
    private Integer applicableYear;
}
