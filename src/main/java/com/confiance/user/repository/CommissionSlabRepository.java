package com.confiance.user.repository;

import com.confiance.user.entity.CommissionSlab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionSlabRepository extends JpaRepository<CommissionSlab, Long> {

    List<CommissionSlab> findByActiveTrue();

    List<CommissionSlab> findByActiveTrueOrderByMinAmountAsc();

    @Query("SELECT c FROM CommissionSlab c WHERE c.active = true AND " +
           "c.minAmount <= :amount AND (c.maxAmount IS NULL OR c.maxAmount >= :amount) " +
           "ORDER BY c.minAmount DESC")
    Optional<CommissionSlab> findSlabForAmount(@Param("amount") BigDecimal amount);

    @Query("SELECT c FROM CommissionSlab c WHERE c.active = true AND " +
           "(c.applicableQuarter IS NULL OR c.applicableQuarter = :quarter) AND " +
           "(c.applicableYear IS NULL OR c.applicableYear = :year) " +
           "ORDER BY c.minAmount ASC")
    List<CommissionSlab> findActiveSlabsForQuarter(@Param("quarter") Integer quarter, @Param("year") Integer year);

    Optional<CommissionSlab> findByNameAndActiveTrue(String name);
}
