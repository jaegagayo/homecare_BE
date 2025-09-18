package jaega.homecare.domain.settlement.dto.res;

import java.math.BigDecimal;

public record GetCaregiverSettlementStatsResponse(
        BigDecimal totalSettlementAmount,
        Long totalWorkMinutes,
        Double totalDistance,
        Long completedCount,
        Long pendingCount
) {}