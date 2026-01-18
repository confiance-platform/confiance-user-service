package com.confiance.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commission_slabs", indexes = {
        @Index(name = "idx_slab_active", columnList = "active"),
        @Index(name = "idx_slab_min_amount", columnList = "minAmount")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CommissionSlab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Slab name for display (e.g., "Standard", "Premium", "VIP")
    @Column(nullable = false)
    private String name;

    // Minimum investment amount for this slab
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal minAmount;

    // Maximum investment amount for this slab (null means unlimited)
    @Column(precision = 19, scale = 2)
    private BigDecimal maxAmount;

    // Commission percentage for this slab
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionPercentage;

    // Description of the slab
    private String description;

    // Whether this slab is active
    @Builder.Default
    private boolean active = true;

    // Quarter and year this slab is applicable to (null means always applicable)
    private Integer applicableQuarter;
    private Integer applicableYear;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
