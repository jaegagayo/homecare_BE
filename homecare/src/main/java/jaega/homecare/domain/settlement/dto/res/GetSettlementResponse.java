package jaega.homecare.domain.settlement.dto.res;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record GetSettlementResponse(
        UUID settlementId,

        String caregiverName,

        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,

        Double settlementAmount,
        Boolean isPaid
) {
}
