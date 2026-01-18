package com.confiance.user.controller;

import com.confiance.common.dto.ApiResponse;
import com.confiance.common.dto.PageResponse;
import com.confiance.user.dto.CommissionSlabRequest;
import com.confiance.user.dto.CommissionSlabResponse;
import com.confiance.user.dto.ReferralResponse;
import com.confiance.user.dto.ReferralSummary;
import com.confiance.user.service.ReferralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/referrals")
@RequiredArgsConstructor
@Tag(name = "Referrals", description = "Referral and Commission Management APIs")
public class ReferralController {

    private final ReferralService referralService;

    // User endpoints
    @GetMapping("/user/{userId}/summary")
    @Operation(summary = "Get Referral Summary", description = "Get referral summary for a user including referral code and commissions")
    public ResponseEntity<ApiResponse<ReferralSummary>> getUserReferralSummary(@PathVariable Long userId) {
        ReferralSummary summary = referralService.getUserReferralSummary(userId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User Referrals", description = "Get paginated list of referrals made by user")
    public ResponseEntity<ApiResponse<PageResponse<ReferralResponse>>> getUserReferrals(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<ReferralResponse> response = referralService.getUserReferrals(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/quarter")
    @Operation(summary = "Get Referrals by Quarter", description = "Get referrals for a specific quarter")
    public ResponseEntity<ApiResponse<List<ReferralResponse>>> getReferralsForQuarter(
            @PathVariable Long userId,
            @RequestParam int quarter,
            @RequestParam int year) {
        List<ReferralResponse> response = referralService.getReferralsForQuarter(userId, quarter, year);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}/commission")
    @Operation(summary = "Get Commission for Quarter", description = "Get commission earned for a specific quarter")
    public ResponseEntity<ApiResponse<BigDecimal>> getCommissionForQuarter(
            @PathVariable Long userId,
            @RequestParam int quarter,
            @RequestParam int year) {
        BigDecimal commission = referralService.getCommissionForQuarter(userId, quarter, year);
        return ResponseEntity.ok(ApiResponse.success(commission));
    }

    // Commission Slab endpoints (Admin)
    @GetMapping("/commission-slabs")
    @Operation(summary = "Get Commission Slabs", description = "Get all active commission slabs")
    public ResponseEntity<ApiResponse<List<CommissionSlabResponse>>> getAllCommissionSlabs() {
        List<CommissionSlabResponse> response = referralService.getAllCommissionSlabs();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/commission-slabs/{id}")
    @Operation(summary = "Get Commission Slab", description = "Get commission slab by ID")
    public ResponseEntity<ApiResponse<CommissionSlabResponse>> getCommissionSlab(@PathVariable Long id) {
        CommissionSlabResponse response = referralService.getCommissionSlabById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/commission-slabs")
    @Operation(summary = "Create Commission Slab", description = "Create a new commission slab (Admin only)")
    public ResponseEntity<ApiResponse<CommissionSlabResponse>> createCommissionSlab(
            @Valid @RequestBody CommissionSlabRequest request) {
        CommissionSlabResponse response = referralService.createCommissionSlab(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Commission slab created successfully", response));
    }

    @PutMapping("/commission-slabs/{id}")
    @Operation(summary = "Update Commission Slab", description = "Update an existing commission slab (Admin only)")
    public ResponseEntity<ApiResponse<CommissionSlabResponse>> updateCommissionSlab(
            @PathVariable Long id,
            @Valid @RequestBody CommissionSlabRequest request) {
        CommissionSlabResponse response = referralService.updateCommissionSlab(id, request);
        return ResponseEntity.ok(ApiResponse.success("Commission slab updated successfully", response));
    }

    @DeleteMapping("/commission-slabs/{id}")
    @Operation(summary = "Delete Commission Slab", description = "Deactivate a commission slab (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteCommissionSlab(@PathVariable Long id) {
        referralService.deleteCommissionSlab(id);
        return ResponseEntity.ok(ApiResponse.success("Commission slab deactivated successfully", null));
    }

    // Admin reporting endpoints
    @GetMapping("/admin/quarter")
    @Operation(summary = "Get All Referrals for Quarter (Admin)", description = "Get all referrals across all users for a quarter")
    public ResponseEntity<ApiResponse<List<ReferralResponse>>> getAllReferralsForQuarter(
            @RequestParam int quarter,
            @RequestParam int year) {
        List<ReferralResponse> response = referralService.getAllReferralsForQuarter(quarter, year);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/admin/{referralId}/mark-paid")
    @Operation(summary = "Mark Referral as Paid (Admin)", description = "Mark a referral commission as paid")
    public ResponseEntity<ApiResponse<Void>> markReferralAsPaid(@PathVariable Long referralId) {
        referralService.markReferralAsPaid(referralId);
        return ResponseEntity.ok(ApiResponse.success("Referral marked as paid", null));
    }
}
