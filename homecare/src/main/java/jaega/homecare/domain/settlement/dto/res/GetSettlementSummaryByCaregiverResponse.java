package jaega.homecare.domain.settlement.dto.res;

import java.math.BigDecimal;

public record GetSettlementSummaryByCaregiverResponse(
        BigDecimal totalAmount,
        Long completedCount,
        Long plannedCount,
        Long cancelledCount
) {
}
