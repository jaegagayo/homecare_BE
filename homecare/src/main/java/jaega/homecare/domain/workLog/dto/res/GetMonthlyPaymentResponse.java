package jaega.homecare.domain.workLog.dto.res;

import java.math.BigDecimal;

public record GetMonthlyPaymentResponse(
        int year,
        int month,
        BigDecimal totalAmount
) {
}
