package jaega.homecare.domain.settlement.dto.res;

import java.math.BigDecimal;

public record GetCaregiverSettlementSummaryResponse(
        BigDecimal totalAmount,
        Long completedCount,
        Long plannedCount,
        Long cancelledCount
) {
}
