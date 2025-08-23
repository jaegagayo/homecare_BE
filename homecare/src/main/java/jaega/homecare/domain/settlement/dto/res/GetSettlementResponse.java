package jaega.homecare.domain.settlement.dto.res;

import java.time.LocalTime;
import java.util.UUID;

public record GetSettlementResponse(
        UUID settlementId,
        LocalTime serviceStartTime,
        LocalTime serviceEndTime,
        Double distanceLog,
        Boolean isPaid
) {
}
