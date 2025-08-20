package jaega.homecare.domain.workMatch.dto.res;

import java.math.BigDecimal;

public record GetMonthlyPaymentResponse(
        int year,
        int month,
        BigDecimal totalAmount
) {
}
