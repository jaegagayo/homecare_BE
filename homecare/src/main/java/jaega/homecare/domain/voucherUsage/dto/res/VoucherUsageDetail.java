package jaega.homecare.domain.voucherUsage.dto.res;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;

import java.time.LocalDate;

public record VoucherUsageDetail(
        LocalDate serviceDate,
        Long amount,
        Long copay,
        String serviceType,
        MatchStatus matchStatus
) {
}
