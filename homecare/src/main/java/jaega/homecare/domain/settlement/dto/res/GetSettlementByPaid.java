package jaega.homecare.domain.settlement.dto.res;

import java.time.LocalDate;
import java.util.UUID;

public record GetSettlementByPaid(
        UUID settlementId,
        LocalDate serviceDate,
        String caregiverName
) {
}