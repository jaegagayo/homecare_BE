package jaega.homecare.domain.workMatch.dto.res;

import java.math.BigDecimal;

public record GetDashboardSettlementResponse(
        BigDecimal totalSettledAmount,
        Long unsettledCount,
        Long fraudAlertCount  // 부정행위 알림 건수
) {
}
