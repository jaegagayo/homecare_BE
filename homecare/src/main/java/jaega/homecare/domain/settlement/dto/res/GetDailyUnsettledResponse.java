package jaega.homecare.domain.settlement.dto.res;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GetDailyUnsettledResponse(
        LocalDate date,
        Long count,
        BigDecimal totalAmount  // 해당 일 미정산 금액
) {
}
