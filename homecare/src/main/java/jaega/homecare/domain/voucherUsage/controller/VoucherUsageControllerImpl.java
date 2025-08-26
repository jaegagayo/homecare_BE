package jaega.homecare.domain.voucherUsage.controller;

import jaega.homecare.domain.voucher.service.query.VoucherQueryService;
import jaega.homecare.domain.voucherUsage.dto.res.VoucherUsageGuideResponse;
import jaega.homecare.domain.voucherUsage.service.query.VoucherUsageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/voucher-usage")
public class VoucherUsageControllerImpl implements VoucherUsageController{

    private final VoucherUsageQueryService voucherUsageQueryService;
    private final VoucherQueryService voucherQueryService;

    @Override
    public ResponseEntity<VoucherUsageGuideResponse> getVoucherUsageGuide(@PathVariable UUID consumerId){
        UUID voucherId = voucherQueryService.getVoucherIdByConsumerId(consumerId);
        Long totalVoucherAmount = voucherQueryService.getTotalAmount(voucherId);

        VoucherUsageGuideResponse response = voucherUsageQueryService.getVoucherUsageGuide(voucherId, totalVoucherAmount);

        return ResponseEntity.ok(response);
    }
}
