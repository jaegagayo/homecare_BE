package jaega.homecare.domain.voucherUsage.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.util.List;

public record MyVoucherUsageResponse(
        Integer grade,
        boolean eligible,
        String voucherMonth,
        Long totalLimit,
        Long usedAmount,
        Long expectedAmount,
        Long remainingAmount,
        Copay copay,
        List<UsageItem> usageList
) {
    public record Copay(Long confirmed, Long expected) {}
    public record UsageItem(String date, ServiceType serviceType, Long amount, Long copay, MatchStatus matchStatus) {}
}
