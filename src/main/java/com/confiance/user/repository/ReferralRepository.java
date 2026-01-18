package com.confiance.user.repository;

import com.confiance.user.entity.Referral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {

    List<Referral> findByReferrerUserId(Long referrerUserId);

    Page<Referral> findByReferrerUserId(Long referrerUserId, Pageable pageable);

    Optional<Referral> findByReferredUserId(Long referredUserId);

    List<Referral> findByReferrerUserIdAndQuarterAndYear(Long referrerUserId, Integer quarter, Integer year);

    @Query("SELECT r FROM Referral r WHERE r.referrerUserId = :userId ORDER BY r.referralDate DESC")
    List<Referral> findReferralsByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(r.commissionEarned) FROM Referral r WHERE r.referrerUserId = :userId")
    BigDecimal getTotalCommissionEarned(@Param("userId") Long userId);

    @Query("SELECT SUM(r.commissionEarned) FROM Referral r WHERE r.referrerUserId = :userId AND r.quarter = :quarter AND r.year = :year")
    BigDecimal getCommissionForQuarter(@Param("userId") Long userId, @Param("quarter") Integer quarter, @Param("year") Integer year);

    @Query("SELECT SUM(r.commissionEarned) FROM Referral r WHERE r.referrerUserId = :userId AND r.paid = false")
    BigDecimal getPendingCommission(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Referral r WHERE r.referrerUserId = :userId")
    Long countReferralsByUser(@Param("userId") Long userId);

    @Query("SELECT r FROM Referral r WHERE r.quarter = :quarter AND r.year = :year ORDER BY r.commissionEarned DESC")
    List<Referral> findReferralsForQuarter(@Param("quarter") Integer quarter, @Param("year") Integer year);

    List<Referral> findByPaidFalse();
}
