package jaega.homecare.domain.voucherUsage.dto.res;

import java.util.List;

public record VoucherUsageResponse(
        Long totalAmount,
        Long usedAmount,
        Long expectedAmount,
        Long remainingAmount,
        Long confirmedCopay,
        Long totalCopay,
        List<VoucherUsageDetail> usageList
) {
}
