package jaega.homecare.domain.voucherUsage.dto.res;

public record VoucherUsageCost(
        long usedAmount,
        long usedCopay,
        long expectedAmount,
        long confirmedCopay
) {
}
