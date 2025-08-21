package jaega.homecare.domain.workLog.dto.res;

import java.math.BigDecimal;

public record GetSettlementCenterSummaryResponse(
        BigDecimal totalAmount,
        Long completedCount,
        Long pendingCount,
        Long rejectedCount
) {}
