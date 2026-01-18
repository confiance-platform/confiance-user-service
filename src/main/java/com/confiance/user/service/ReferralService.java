package com.confiance.user.service;

import com.confiance.common.dto.PageResponse;
import com.confiance.common.exception.ResourceNotFoundException;
import com.confiance.user.dto.CommissionSlabRequest;
import com.confiance.user.dto.CommissionSlabResponse;
import com.confiance.user.dto.ReferralResponse;
import com.confiance.user.dto.ReferralSummary;
import com.confiance.user.entity.CommissionSlab;
import com.confiance.user.entity.Referral;
import com.confiance.user.entity.User;
import com.confiance.user.repository.CommissionSlabRepository;
import com.confiance.user.repository.ReferralRepository;
import com.confiance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final CommissionSlabRepository commissionSlabRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createReferral(Long referrerUserId, Long referredUserId, String referredUserName) {
        log.info("Creating referral: referrer={}, referred={}", referrerUserId, referredUserId);

        LocalDate today = LocalDate.now();
        int quarter = Referral.getQuarterFromDate(today);
        int year = today.getYear();

        Referral referral = Referral.builder()
                .referrerUserId(referrerUserId)
                .referredUserId(referredUserId)
                .referredUserName(referredUserName)
                .referralDate(today)
                .quarter(quarter)
                .year(year)
                .status("PENDING")
                .build();

        referralRepository.save(referral);
    }

    @Transactional
    public void updateReferralInvestment(Long referredUserId, BigDecimal investmentAmount) {
        referralRepository.findByReferredUserId(referredUserId)
                .ifPresent(referral -> {
                    referral.setReferredUserInvestment(referral.getReferredUserInvestment().add(investmentAmount));
                    referral.setStatus("ACTIVE");

                    // Calculate commission based on slab
                    Optional<CommissionSlab> slab = commissionSlabRepository.findSlabForAmount(referral.getReferredUserInvestment());
                    slab.ifPresent(s -> {
                        BigDecimal commission = investmentAmount
                                .multiply(s.getCommissionPercentage())
                                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
                        referral.setCommissionRate(s.getCommissionPercentage());
                        referral.setCommissionEarned(referral.getCommissionEarned().add(commission));
                    });

                    referralRepository.save(referral);
                });
    }

    public ReferralSummary getUserReferralSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Long totalReferrals = referralRepository.countReferralsByUser(userId);
        BigDecimal totalCommission = referralRepository.getTotalCommissionEarned(userId);
        BigDecimal pendingCommission = referralRepository.getPendingCommission(userId);
        List<Referral> recentReferrals = referralRepository.findReferralsByUser(userId);

        return ReferralSummary.builder()
                .userId(userId)
                .referralCode(user.getReferralCode())
                .totalReferrals(totalReferrals != null ? totalReferrals : 0L)
                .totalCommissionEarned(totalCommission != null ? totalCommission : BigDecimal.ZERO)
                .pendingCommission(pendingCommission != null ? pendingCommission : BigDecimal.ZERO)
                .recentReferrals(recentReferrals.stream().map(this::toReferralResponse).toList())
                .build();
    }

    public PageResponse<ReferralResponse> getUserReferrals(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("referralDate").descending());
        Page<Referral> referralPage = referralRepository.findByReferrerUserId(userId, pageable);
        return buildReferralPageResponse(referralPage);
    }

    public List<ReferralResponse> getReferralsForQuarter(Long userId, int quarter, int year) {
        return referralRepository.findByReferrerUserIdAndQuarterAndYear(userId, quarter, year)
                .stream()
                .map(this::toReferralResponse)
                .toList();
    }

    public BigDecimal getCommissionForQuarter(Long userId, int quarter, int year) {
        BigDecimal commission = referralRepository.getCommissionForQuarter(userId, quarter, year);
        return commission != null ? commission : BigDecimal.ZERO;
    }

    // Commission Slab Management
    public List<CommissionSlabResponse> getAllCommissionSlabs() {
        return commissionSlabRepository.findByActiveTrueOrderByMinAmountAsc()
                .stream()
                .map(this::toSlabResponse)
                .toList();
    }

    public CommissionSlabResponse getCommissionSlabById(Long id) {
        return commissionSlabRepository.findById(id)
                .map(this::toSlabResponse)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionSlab", "id", id));
    }

    @Transactional
    public CommissionSlabResponse createCommissionSlab(CommissionSlabRequest request) {
        CommissionSlab slab = CommissionSlab.builder()
                .name(request.getName())
                .minAmount(request.getMinAmount())
                .maxAmount(request.getMaxAmount())
                .commissionPercentage(request.getCommissionPercentage())
                .description(request.getDescription())
                .active(request.isActive())
                .applicableQuarter(request.getApplicableQuarter())
                .applicableYear(request.getApplicableYear())
                .build();

        CommissionSlab saved = commissionSlabRepository.save(slab);
        return toSlabResponse(saved);
    }

    @Transactional
    public CommissionSlabResponse updateCommissionSlab(Long id, CommissionSlabRequest request) {
        CommissionSlab slab = commissionSlabRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionSlab", "id", id));

        if (request.getName() != null) slab.setName(request.getName());
        if (request.getMinAmount() != null) slab.setMinAmount(request.getMinAmount());
        if (request.getMaxAmount() != null) slab.setMaxAmount(request.getMaxAmount());
        if (request.getCommissionPercentage() != null) slab.setCommissionPercentage(request.getCommissionPercentage());
        if (request.getDescription() != null) slab.setDescription(request.getDescription());
        slab.setActive(request.isActive());
        if (request.getApplicableQuarter() != null) slab.setApplicableQuarter(request.getApplicableQuarter());
        if (request.getApplicableYear() != null) slab.setApplicableYear(request.getApplicableYear());

        CommissionSlab saved = commissionSlabRepository.save(slab);
        return toSlabResponse(saved);
    }

    @Transactional
    public void deleteCommissionSlab(Long id) {
        CommissionSlab slab = commissionSlabRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CommissionSlab", "id", id));
        slab.setActive(false);
        commissionSlabRepository.save(slab);
    }

    // Admin methods
    public List<ReferralResponse> getAllReferralsForQuarter(int quarter, int year) {
        return referralRepository.findReferralsForQuarter(quarter, year)
                .stream()
                .map(this::toReferralResponse)
                .toList();
    }

    @Transactional
    public void markReferralAsPaid(Long referralId) {
        Referral referral = referralRepository.findById(referralId)
                .orElseThrow(() -> new ResourceNotFoundException("Referral", "id", referralId));
        referral.setPaid(true);
        referral.setPaidDate(LocalDate.now());
        referral.setStatus("PAID");
        referralRepository.save(referral);
    }

    private ReferralResponse toReferralResponse(Referral r) {
        return ReferralResponse.builder()
                .id(r.getId())
                .referrerUserId(r.getReferrerUserId())
                .referredUserId(r.getReferredUserId())
                .referredUserName(r.getReferredUserName())
                .referralDate(r.getReferralDate())
                .quarter(r.getQuarter())
                .year(r.getYear())
                .referredUserInvestment(r.getReferredUserInvestment())
                .commissionRate(r.getCommissionRate())
                .commissionEarned(r.getCommissionEarned())
                .status(r.getStatus())
                .paid(r.isPaid())
                .paidDate(r.getPaidDate())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private CommissionSlabResponse toSlabResponse(CommissionSlab s) {
        return CommissionSlabResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .minAmount(s.getMinAmount())
                .maxAmount(s.getMaxAmount())
                .commissionPercentage(s.getCommissionPercentage())
                .description(s.getDescription())
                .active(s.isActive())
                .applicableQuarter(s.getApplicableQuarter())
                .applicableYear(s.getApplicableYear())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    private PageResponse<ReferralResponse> buildReferralPageResponse(Page<Referral> page) {
        return PageResponse.<ReferralResponse>builder()
                .content(page.getContent().stream().map(this::toReferralResponse).toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .build();
    }
}
