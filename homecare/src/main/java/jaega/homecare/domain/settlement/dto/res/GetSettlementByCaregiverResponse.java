package jaega.homecare.domain.settlement.dto.res;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record GetSettlementByCaregiverResponse(
        String centerName,
        UUID settlementId,
        String caregiverName,
        LocalDate serviceDate,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        Double settlementAmount,
        Double distanceLog,
        Boolean isPaid
) {
}
