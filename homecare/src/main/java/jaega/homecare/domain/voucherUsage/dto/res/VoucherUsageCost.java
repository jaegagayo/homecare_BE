package jaega.homecare.domain.voucherUsage.dto.res;

public record VoucherUsageCost(
        long usedAmount,
        long expectedAmount,
        long confirmedCopay,
        long totalCopay
) {
}
